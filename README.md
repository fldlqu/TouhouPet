# TouhouPet (逆向重建 + 现代化)

从 APK 逆向重建的 Android 项目。原应用 **TouhouPet v1.0.2**（package `k.p.main`）是一个
桌面宠物类应用（2012–2013 年开发，东方 Project 主题，宠物为古明地觉 Satori），
使用 `SYSTEM_ALERT_WINDOW` 悬浮窗常驻桌面，数据存放在 SD 卡 `TouhouPet` 文件夹。

**分支说明**:
- `original` —— 行为保真基线:与 2012 年原版逐方法一致(255/255 方法级比对通过),仅保证能构建;
  在现代 Android 上无法运行(Android 8+ 禁止 TYPE_PHONE 悬浮窗)。
- `main` —— 现代化版(本分支):在保真基线上做现代系统适配,宠物逻辑/存档格式不变。

## 项目结构

```
app/src/main/
├── AndroidManifest.xml        # apktool 解码（二进制 XML → 可读 XML）
├── java/                      # jadx 1.5.5 反编译的 Java 源码（139 个文件）
│   ├── k/p/...                # 应用本体
│   └── local/kcn/...          # 开发者自用库（日志/数学/视图）
├── res/
│   ├── drawable/              # 原图（36 张，含 progress_rate.9.png）
│   ├── layout/                # apktool 解码的布局（11 个）
│   └── values/ids.xml         # 从 resources.arsc 恢复的 id 声明
└── jniLibs/
    ├── arm64-v8a/libencrypt.so    # 反汇编还原的 C 源码编译（等价实现，见 tools/）
    └── armeabi-v7a/libencrypt.so  # 同上（32 位；原版 armeabi 带 TEXTREL，targetSdk≥23 被 linker 拒绝，已替换）
```

## 64 位支持（arm64-v8a）

原版 APK 只有 `armeabi`（32 位）。`libencrypt.so` 经反汇编完全还原（仅 236 字节实现）：

- 导出两个 JNI 函数 `encryptFile` / `decipheringFile`，都跳转到同一实现（XOR 对称）；
- 算法：文件名加 `.buf` 后缀 → 逐块(1024B)读取 → 每字节 `^= 0x0C` → 写回 → 删原名 → rename 回来；
- 不依赖任何加密库（fopen/fread/fwrite 而已），无开源项目可替代。

`tools/build_encrypt.sh` 编译 `arm64-v8a` 版本（源码 `app/src/main/jni/libencrypt.c`，
JNI 函数表索引硬编码 169/170，无需 NDK）。验证链：

1. **反汇编推导**（`llvm-objdump -d --triple=thumbv7-linux-gnueabi`）；
2. **新 so 黑盒测试**（`tools/test_encrypt.c` 伪造 JNIEnv 调用，加密/往返/空文件全部通过）；
3. **原版 so 真机对照**（`tools/mini_loader.c` 自写 ELF loader 在 32 位兼容模式加载原版 so，
   绕过现代 linker 的 TEXTREL 拒绝，**原版输出与推导算法逐字节一致**）。

arm64 设备加载 arm64-v8a；32 位设备加载 armeabi-v7a（重新编译版，无 TEXTREL，
算法与原版逐字节一致——黑盒测试 VERIFIED）。原版 armeabi 文件保留在 original 分支。

## 构建

已在本机（Termux aarch64 + Android SDK 36 + build-tools 36）**实际构建验证通过**：

```bash
./gradlew assembleDebug   # → app/build/outputs/apk/debug/app-debug.apk
./gradlew assembleRelease # → app/build/outputs/apk/release/app-release.apk(debug key 签名)
./tools/align16k.sh       # → *-16k.apk:Android 16 (16KB 页)兼容的 16KB 对齐版
```

工具链：Gradle 9.6.1 / AGP 9.3.1 / JDK 26 / compileSdk 35，
**main 分支:`minSdk 26 / targetSdk 36 / versionCode 4 / versionName 1.1.0`**；
`original` 分支保留原版 `minSdk 8 / targetSdk 15 / versionCode 3 / versionName 1.0.2`。

本机特殊配置（已放 `~/.gradle/gradle.properties`，不影响项目可移植性）：
- `android.aapt2FromMavenOverride`：AGP 自带的 aapt2 只有 x86-64 版，ARM 设备需指向 SDK build-tools 的原生版。
- 项目内 `gradle.properties` 设了 `android.builtInKotlin=false`：AGP 9 内置 Kotlin 会往 APK 里塞 kotlin-stdlib（约 1100 个类），原版没有。

## 逆向流程

1. `zip` 重新打包 APK → `apktool d` 解码 manifest / 布局 / 资源表 → `jadx` 反编译 dex → Java。
2. 资源一致性校验：原 `R.java`（从 dex 恢复）中全部 74 个字段逐一对照 `res/` 实际文件，无缺失。
3. 编译验证：全部源码用 `javac -bootclasspath android-17.jar`（API 17，与原版同期）编译，**0 错误**。
4. 行为保真校验：重编译产物与原始 smali 做**逐类方法级比对**（255 个类，排除 3 个有意删除的类），**完全一致**。
5. **完整构建校验**（AGP 9.3.1 + SDK 35）：产物 dex 与原始 classes.dex 做 dex 级类清单比对——
   原始 258 类 vs 构建 255 类，差异仅 3 个有意为之的类（2 个 SDK 桩类 + 空 R$attr）；
   资源表名称一致；37 张图片逐字节相同；布局经 aapt2 xmltree 比对语义一致；libencrypt.so md5 相同。

## 对反编译产物的修正（均有 smali 佐证）

| 文件 | jadx 产物问题 | 修正 |
|---|---|---|
| `local/kcn/utils/LogUtil.java` | 4 个 `log()` 声明 `throws Throwable`，导致所有覆写方法无法编译；`fw` 未初始化；`th` 变量越界 | 移除 throws（dex 的 throws 注解对 dalvik 无语义）；`fw = null`（smali 有 `const/4 v2, 0x0` 佐证）；catch 块重写 |
| `k/p/main/TouhouPet.java` | `onCreate` 后多出一行越界的 `LogUtil.log(ae)` | 删除（smali 中 catch 处理器直接 `goto return`） |
| `k/p/animation/PetAnimationLoader.java` | 整个 `load()` 反编译错误（jadx 明确标注 decompiled incorrectly） | 按 smali 逐指令重建。**注意**：原版二进制每轮循环调用两次 `parser.next()`（第二次结果丢弃，.line 31/64 佐证），该怪癖按原样保留 |
| `k/p/utils/SaveLoadUtil.java` | `save()` 变量作用域错乱；`load()` 结构错误 | 按 smali 重建：save = try/catch(重抛)/finally(close+encrypt)；load = decipher→读→StreamCorruptedException 时再 decipher 重试，无 finally；补回 jadx 遗漏的 `clear()` |
| `k/p/view/sliderview/SliderView.java` | jadx 把匿名类 `SliderView$2` 提升为具名类 `AnonymousClass2`；`getStatus()` 丢失 `check-cast` | 还原为内联匿名类；按 smali 的 `check-cast Ljava/lang/String;` 补 `(String)` 强转 |
| `k/p/view/sliderview/LocationSliderItemList.java` | 双重匿名类中引用 `AnonymousClass1.this`（Java 源无法表达） | 改为 `LocationSliderItemList.this`（同一对象，行为等价） |
| `local/kcn/utils/MathUtil.java` | `FloatMath.sqrt` 在 API 23 已移除，API 35 编译失败 | 改为 `(float) Math.sqrt(...)`（FloatMath.sqrt 内部即此实现） |
| 各覆写方法上的 `throws Throwable` | dex 元数据与 javac 覆写规则冲突 | 移除（仅编译期元数据，dalvik 忽略） |
| `android/annotation/SuppressLint`、`TargetApi` | 原项目自带的 SDK 桩类 | 删除（现代 android.jar 自带，保留会与 SDK 类冲突） |
| `R.java`、`BuildConfig.java` | jadx 从 dex 恢复的生成类 | 删除（构建时由 aapt2 / AGP 重新生成） |

## 现代化改动（main 分支，相对 original 基线）

| 改动 | 说明 |
|---|---|
| 悬浮窗类型 | 全部 8 处 `TYPE_PHONE`(2003)→ `TYPE_APPLICATION_OVERLAY`(Android 8+ 唯一可用) |
| 悬浮窗权限引导 | 启动时检查 `Settings.canDrawOverlays()`,缺失则弹窗跳系统设置,返回后自动继续 |
| 数据目录 | `/sdcard/TouhouPet` → `getExternalFilesDir(null)/TouhouPet`(应用专属,免存储权限,卸载即清) |
| 旧数据迁移 | 首次启动检测旧版 SD 卡目录并整体拷贝到新目录(存档/动画/音乐全部保留) |
| 前台服务 | `startForeground` 带 `specialUse` 类型 + 声明(Android 14+ 必须);通知改用 Channel + Builder;PendingIntent 加 `FLAG_IMMUTABLE`(targetSdk 31+ 必须) |
| 权限声明 | 移除 `WRITE_EXTERNAL_STORAGE`;新增 `POST_NOTIFICATIONS`(Android 13+ 运行时请求,不阻塞);移除无效的 `persistent="true"` |
| SDK 级别 | `minSdk 8→26`(Android 8.0)、`targetSdk 15→36`(Android 16) |
| 16KB 页 | `extractNativeLibs=false` 时 so 从 zip 直接映射;`tools/align16k.sh` 产出 16KB 对齐 APK(16KB 页设备必需) |
| 图标 | 自适应图标(adaptive icon,深蓝灰底 + 原版宠物图) |
| Application | 新增 `PetApplication` 统一初始化(数据目录、通知渠道) |
| 版本号 | versionCode 3→4, versionName 1.0.2→1.1.0 |

**未改动**(行为保真):宠物逻辑/状态机/存档格式与加解密(SUID 兼容旧存档)、UI 布局与图片、动画加载。

**首次启动流程**:授权悬浮窗 → (有旧数据则自动迁移)→ 无数据目录则提示放置数据
(动画/音乐数据原版就不在 APK 内,需用户提供 `pet/`、`system/` 等目录)。

## 已知限制 / 待验证

- **未做真机/模拟器运行验证**。已验证到 APK 构建产物级：dex 类清单、资源表、图片字节、布局语义、原生库 md5 均与原版一致。
- **资源 ID 数值不同**：原版（2012 年 aapt）drawable 从 `0x7f02xxxx` 起，aapt2 从 `0x7f01xxxx` 起。
  构建版内部完全自洽（代码/资源表引用一致），运行时无影响；仅当有外部硬编码 ID 时才会差异。
- 原应用用 `WindowManager.LayoutParams.TYPE_PHONE`(2003) 悬浮窗 —— Android 8.0+ 已禁止，
  需改为 `TYPE_APPLICATION_OVERLAY` 才能在当代系统运行（移植工作，未包含）。
- 应用数据（宠物存档/动画 XML）存于 SD 卡 `TouhouPet/` 目录，不在 APK 内，无法从本 APK 恢复。
- 原版 `libencrypt.so` 仅含 armeabi 且带 TEXTREL（2012 年产物，targetSdk<23 豁免）；
  本项目以反汇编还原源码重新编译 `arm64-v8a` + `armeabi-v7a`（见上节），行为等价、无 TEXTREL，
  16KB 页设备用 `tools/align16k.sh` 的对齐版。
- 编译验证用的是 API 17 的 android.jar；若用更高 compileSdk 构建，`@SuppressLint` 等标注
  行为一致，但悬浮窗 API 需按上条调整。

## 中间产物

- `~/thpetr-work/`：`app.apk`（重组）、`apktool-decoded/`、`jadx-out/`（含完整 jadx 原始输出）、`android-17.jar`（验证用）
- `~/thpetr/`：原始 APK 解包文件（未改动）
