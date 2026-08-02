# TouhouPet (逆向重建 + 现代化)

从 APK 逆向重建的 Android 项目。原应用 **TouhouPet v1.0.2**（package `k.p.main`）是一个
桌面宠物类应用（2012–2013 年开发，东方 Project 主题，宠物为古明地觉 Satori），
使用 `SYSTEM_ALERT_WINDOW` 悬浮窗常驻桌面，数据存放在 SD 卡 `TouhouPet` 文件夹。

**分支说明**:
- `original` —— 行为保真基线:与 2012 年原版逐方法一致(255/255 方法级比对通过),仅保证能构建;
  在现代 Android 上无法运行(Android 8+ 禁止 TYPE_PHONE 悬浮窗)。
- `main` —— 现代化版(本分支):在保真基线上做 Kotlin 迁移 + 现代系统适配,
  宠物逻辑/存档格式不变,旧存档可直接使用。

## 项目结构

```
app/src/main/
├── AndroidManifest.xml        # apktool 解码(二进制 XML → 可读 XML)
├── kotlin/                    # Kotlin 迁移后的业务代码(81 个文件,含 k/p/modern 新基础设施)
│   ├── k/p/modern/            # 新增:协程基础设施(AppScopes/Tasks/Poller/GameLoop)、DevPanel 开发者面板、Diag 诊断探针
│   ├── k/p/...                # 应用本体(Kotlin)
│   └── local/kcn/...          # 开发者自用库(日志/数学/视图)
├── java/                      # 保留 Java 的序列化/存档领域类(67 个,存档兼容红线,见下)
│   └── k/p/{action,domain,item,location}/...
├── res/
│   ├── drawable/              # 原图(37 张已转无损 WebP,progress_rate.9.png 保持 PNG)
│   ├── layout/                # apktool 解码的布局(10 个,音乐菜单新增"选择音乐文件夹"入口)
│   └── values/ids.xml         # 从 resources.arsc 恢复的 id 声明
└── jniLibs/
    ├── arm64-v8a/libencrypt.so    # 反汇编还原的 C 源码编译(等价实现,见 tools/)
    └── armeabi-v7a/libencrypt.so  # 同上(32 位;原版 armeabi 带 TEXTREL,targetSdk≥23 被 linker 拒绝,已替换)
```

## 64 位支持(arm64-v8a)

原版 APK 只有 `armeabi`(32 位)。`libencrypt.so` 经反汇编完全还原(仅 236 字节实现):

- 导出两个 JNI 函数 `encryptFile` / `decipheringFile`,都跳转到同一实现(XOR 对称);
- 算法:文件名加 `.buf` 后缀 → 逐块(1024B)读取 → 每字节 `^= 0x0C` → 写回 → 删原名 → rename 回来;
- 不依赖任何加密库(fopen/fread/fwrite 而已),无开源项目可替代。

`tools/build_encrypt.sh` 编译 `arm64-v8a` 版本(源码 `app/src/main/jni/libencrypt.c`,
JNI 函数表索引硬编码 169/170,无需 NDK)。验证链:

1. **反汇编推导**(`llvm-objdump -d --triple=thumbv7-linux-gnueabi`);
2. **新 so 黑盒测试**(`tools/test_encrypt.c` 伪造 JNIEnv 调用,加密/往返/空文件全部通过);
3. **原版 so 真机对照**(`tools/mini_loader.c` 自写 ELF loader 在 32 位兼容模式加载原版 so,
   绕过现代 linker 的 TEXTREL 拒绝,**原版输出与推导算法逐字节一致**)。

arm64 设备加载 arm64-v8a;32 位设备加载 armeabi-v7a(重新编译版,无 TEXTREL,
算法与原版逐字节一致——黑盒测试 VERIFIED)。原版 armeabi 文件保留在 original 分支。

## 构建

已在本机(Termux aarch64 + Android SDK 36 + build-tools 36)**实际构建验证通过**:

```bash
./gradlew assembleDebug   # → app/build/outputs/apk/debug/app-debug.apk
./gradlew assembleRelease # → app/build/outputs/apk/release/app-release.apk(debug key 签名)
```

AGP 9.3.1 默认将 .so 按 16KB 页对齐(`android.nativeLibraryAlignmentPageSize` 默认 16384),
产物直接兼容 Android 16 的 16KB 页设备,无需额外处理。

工具链:Gradle 9.6.1 / AGP 9.3.1 / JDK 26 / compileSdk 36,
Java 与 Kotlin 目标均为 24(内置 Kotlin 最高支持 JVM target 24),
依赖 `kotlinx-coroutines-android:1.10.2`(AGP 9 内置 Kotlin,无需额外插件)。
**main 分支:`minSdk 26 / targetSdk 36 / versionCode 4 / versionName 1.1.0`**;
`original` 分支保留原版 `minSdk 8 / targetSdk 15 / versionCode 3 / versionName 1.0.2`。

本机特殊配置(已放 `~/.gradle/gradle.properties`,不影响项目可移植性):
- `android.aapt2FromMavenOverride`:AGP 自带的 aapt2 只有 x86-64 版,ARM 设备需指向 SDK build-tools 的原生版。
- Kotlin 用 AGP 9 内置支持(无需 `kotlin-android` 插件),仅声明 `kotlinx-coroutines-android` 依赖。

## 逆向流程

1. `zip` 重新打包 APK → `apktool d` 解码 manifest / 布局 / 资源表 → `jadx` 反编译 dex → Java。
2. 资源一致性校验:原 `R.java`(从 dex 恢复)中全部 74 个字段逐一对照 `res/` 实际文件,无缺失。
3. 编译验证:全部源码用 `javac -bootclasspath android-17.jar`(API 17,与原版同期)编译,**0 错误**。
4. 行为保真校验:重编译产物与原始 smali 做**逐类方法级比对**(255 个类,排除 3 个有意删除的类),**完全一致**。
5. **完整构建校验**(AGP 9.3.1 + SDK 35):产物 dex 与原始 classes.dex 做 dex 级类清单比对——
   原始 258 类 vs 构建 255 类,差异仅 3 个有意为之的类(2 个 SDK 桩类 + 空 R$attr);
   资源表名称一致;37 张图片逐字节相同;布局经 aapt2 xmltree 比对语义一致;libencrypt.so md5 相同。

## 对反编译产物的修正(均有 smali 佐证)

以下修正发生在逆向重建阶段(Java 产物,现均已随 Kotlin 迁移保留语义;迁移时另有
`tools/check_strings.py` 校验 Java 原文与 Kotlin 稿的字符串字面量集合一致):

| 文件 | jadx 产物问题 | 修正 |
|---|---|---|
| `local/kcn/utils/LogUtil.java` | 4 个 `log()` 声明 `throws Throwable`,导致所有覆写方法无法编译;`fw` 未初始化;`th` 变量越界 | 移除 throws(dex 的 throws 注解对 dalvik 无语义);`fw = null`(smali 有 `const/4 v2, 0x0` 佐证);catch 块重写 |
| `k/p/main/TouhouPet.java` | `onCreate` 后多出一行越界的 `LogUtil.log(ae)` | 删除(smali 中 catch 处理器直接 `goto return`) |
| `k/p/animation/PetAnimationLoader.java` | 整个 `load()` 反编译错误(jadx 明确标注 decompiled incorrectly) | 按 smali 逐指令重建。**注意**:原版二进制每轮循环调用两次 `parser.next()`(第二次结果丢弃,.line 31/64 佐证),该怪癖按原样保留 |
| `k/p/utils/SaveLoadUtil.java` | `save()` 变量作用域错乱;`load()` 结构错误 | 按 smali 重建:save = try/catch(重抛)/finally(close+encrypt);load = decipher→读→StreamCorruptedException 时再 decipher 重试,无 finally;补回 jadx 遗漏的 `clear()` |
| `k/p/view/sliderview/SliderView.java` | jadx 把匿名类 `SliderView$2` 提升为具名类 `AnonymousClass2`;`getStatus()` 丢失 `check-cast` | 还原为内联匿名类;按 smali 的 `check-cast Ljava/lang/String;` 补 `(String)` 强转 |
| `k/p/view/sliderview/LocationSliderItemList.java` | 双重匿名类中引用 `AnonymousClass1.this`(Java 源无法表达) | 改为 `LocationSliderItemList.this`(同一对象,行为等价) |
| `local/kcn/utils/MathUtil.java` | `FloatMath.sqrt` 在 API 23 已移除,API 35 编译失败 | 改为 `(float) Math.sqrt(...)`(FloatMath.sqrt 内部即此实现) |
| 各覆写方法上的 `throws Throwable` | dex 元数据与 javac 覆写规则冲突 | 移除(仅编译期元数据,dalvik 忽略) |
| `android/annotation/SuppressLint`、`TargetApi` | 原项目自带的 SDK 桩类 | 删除(现代 android.jar 自带,保留会与 SDK 类冲突) |
| `R.java`、`BuildConfig.java` | jadx 从 dex 恢复的生成类 | 删除(构建时由 aapt2 / AGP 重新生成) |

## 现代化改动(main 分支,相对 original 基线)

### Kotlin 迁移与架构

| 改动 | 说明 |
|---|---|
| Kotlin 全量迁移 | 8 批共 71 个 Java 文件迁至 Kotlin,全部通过 `tools/check_strings.py` 台词/字符串逐字保真校验;`gen_location.kt.py` 等机械转换保证字符串零改动 |
| 序列化类保留 Java | **67 个存档相关领域类**(action/domain/item/location)保持 Java 不动——涉及 Java 序列化格式,迁移会破坏旧存档兼容(红线) |
| 协程基础设施 | 新增 `k/p/modern/`:AppScopes(统一作用域工厂)、Tasks(Java 侧可用的协程后台任务)、Poller(替代 while+Thread.sleep 轮询)、GameLoop(替代裸 Thread 游戏循环) |
| 绘制线程 | `BaseSurfaceView` 协程化,每视图一条单线程 dispatcher(lockCanvas/unlockCanvasAndPost 要求同线程配对;`limitedParallelism(1)` 挂起后会换 worker 导致跨线程 unlock 崩溃,必须用 `newSingleThreadExecutor`) |
| 服务实例化 | MainService 游戏循环/服务线程协程化,静态服务统一实例化 |
| 开发者面板 | DevPanel:悬浮窗面板,状态切换/属性调节/时间加速(×1/×60/×600)/随机动画/模拟升级/查看崩溃日志/手动崩溃测试;入口为通知栏"状态"文字点击,由 `BuildConfig.DEV_PANEL` 控制(自用测试默认开,发布改 false) |
| 诊断探针 | `k/p/modern/Diag.kt`:启动链路打点到 `/sdcard/TouhouPet/system/log/app.log`(临时探针,可 disable) |

### 系统适配

| 改动 | 说明 |
|---|---|
| 悬浮窗类型 | 全部 8 处 `TYPE_PHONE`(2003)→ `TYPE_APPLICATION_OVERLAY`(Android 8+ 唯一可用) |
| 权限引导链 | 启动依次引导:悬浮窗 → 存储访问(Android 11+ 引导"所有文件访问" MANAGE_EXTERNAL_STORAGE;API 26–29 用 WRITE_EXTERNAL_STORAGE 运行时权限)→ 电池优化例外(一次,非阻塞) |
| 数据目录 | **与原版一致**:公共 SD 卡 `/sdcard/TouhouPet`(卸载不丢数据,旧存档/动画/音乐授权后直接可用;曾尝试 `getExternalFilesDir` + 拷贝迁移,按用户决策放弃) |
| 前台服务 | `startForeground` 带 `specialUse` 类型 + 声明(Android 14+ 必须);通知改用 Channel + Builder;PendingIntent 加 `FLAG_IMMUTABLE`(targetSdk 31+ 必须) |
| 媒体会话 | 音乐播放对接 MediaSession:播放状态/歌名/歌手/时长同步进系统,系统媒体控制回调(播放/暂停/上下曲)接回原播放逻辑,退出时释放会话 |
| 音频焦点 | SongService 请求音频焦点:被打断(transient)暂停并在恢复后自动续播,可闪避(duck)时降音量,永久失去焦点则停止 |
| 开机自启 | BootReceiver + RECEIVE_BOOT_COMPLETED:服务存活标记存在时开机自动恢复桌面宠物;主动退出(exit/exitWithoutSave)清除标记 |
| 音乐来源 | 音乐菜单新增"选择音乐文件夹/全部歌曲":逐级目录浏览选择并持久化,递归扫描 mp3/wma/ogg/wav/flac/m4a/aac/opus;未选时保持全库扫描;文件夹失效自动回退全库 |
| 权限声明 | `POST_NOTIFICATIONS`(Android 13+ 运行时请求,不阻塞);`MANAGE_EXTERNAL_STORAGE` + `WRITE_EXTERNAL_STORAGE`(maxSdk 29);移除无效的 `persistent="true"` |
| SDK 级别 | `minSdk 8→26`(Android 8.0)、`targetSdk 15→36`(Android 16)、`compileSdk 36` |
| 16KB 页 | `extractNativeLibs=false` 时 so 从 zip 直接映射;AGP 9.3.1 默认按 16KB 对齐打包,产物直接兼容 16KB 页设备 |
| 图标 | 自适应图标(adaptive icon,深蓝灰底 + 原版宠物图) |
| 图片格式 | 37 张 PNG → 无损 WebP(省 35%,全量逐像素验证一致;9-patch 必须保持 PNG);bg.jpg → 有损 WebP q85(223KB → 145KB,PSNR 36.9dB) |
| Application | 新增 `PetApplication` 统一初始化(数据目录、通知渠道) |
| 版本号 | versionCode 3→4, versionName 1.0.2→1.1.0 |

### 真机稳定性修复

| 问题 | 修复 |
|---|---|
| 首次点击播放无声 | MediaPlayer 需先 `prepare()` 再 `start()` |
| 音乐最后一首点"下一首"闪退 | 越界回绕到第一首(原版遗留崩溃级 bug,不因保真保留) |
| PendingIntent FLAG_MUTABLE 误用闪退 | 改用 `FLAG_IMMUTABLE` |
| LogUtil.recordPath / PetView 动画空指针 | 判空防御 |
| DesktopService NPE、exit 非幂等 | 幂等化 + listener 防重复注册 |
| 绘制循环 IllegalMonitorStateException | 协程 `limitedParallelism(1)` 跨线程 unlock → 单线程 executor |
| 宠物白屏 | 动画 XML 解析器推进修正 + Kotlin `split`(正则)与 Java `split`(字面量)语义差异修正 |

**未改动**(行为保真):宠物逻辑/状态机/存档格式与加解密(SUID 兼容旧存档)、
UI 布局与图片、动画加载;67 个序列化领域类保持 Java(存档兼容红线)。

**首次启动流程**:授权悬浮窗 → 授权存储访问(Android 11+ "所有文件访问")→ (一次)电池优化引导 →
无数据目录则提示放置数据(动画/音乐数据原版就不在 APK 内,需用户提供 `pet/`、`system/` 等目录)。

## 已知限制 / 待验证
- 原版 `libencrypt.so` 仅含 armeabi 且带 TEXTREL(2012 年产物,targetSdk<23 豁免);
  本项目以反汇编还原源码重新编译 `arm64-v8a` + `armeabi-v7a`(见上节),行为等价、无 TEXTREL,
  16KB 页设备直接可用(AGP 默认 16KB 对齐)。
- Android 11+ 需要"所有文件访问"权限才能读写 `/sdcard/TouhouPet`(系统设置内手动开启,
  应用只能引导到设置页,无法直接弹运行时授权)。
- `DEV_PANEL` 构建开关默认 `true`(自用测试);对外发布前应改为 `false`。
- `Diag` 诊断探针仍在代码中,问题定位后应收敛或移除。

## 中间产物与工具

- `~/thpetr-work/`:`app.apk`(重组)、`apktool-decoded/`、`jadx-out/`(含完整 jadx 原始输出)、`android-17.jar`(验证用)
- `~/thpetr/`:原始 APK 解包文件(未改动)
- `tools/`:`build_encrypt.sh`(so 编译)、`test_encrypt.c` / `mini_loader.c` / `test_orig.c`(so 验证)、
  `check_strings.py`(Kotlin 迁移字符串保真校验)、`gen_location.kt.py`(机械转换脚本)
