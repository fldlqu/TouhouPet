# TouhouPet

本项目是 2012 年发布的 **TouhouPet v1.0.2** 的逆向重建 + 现代化版本：
原版因使用已被 Android 8 禁止的旧式悬浮窗而无法在现代系统上运行，本版本在保持
宠物逻辑与存档格式不变的前提下，重新适配了当代 Android 系统。


## 兼容性

- Android 8.0（API 26）及以上
- 支持 arm64-v8a 与 armeabi-v7a（32 位）设备

## 安装

目前尚未发布预编译 APK,请自行构建(见下文)后侧载安装。

## 快速开始

首次启动会依次引导授权，按提示操作即可：

1. **悬浮窗权限**（必须，宠物以悬浮窗显示）
2. **存储访问**：Android 11+ 需授予"所有文件访问"权限
3. **电池优化例外**（建议）：避免宠物被系统后台清理

### 数据目录

宠物数据存放在 **`/sdcard/TouhouPet`**（与原版一致，卸载应用不会删除）。
宠物动画与音乐数据不在 APK 内，需自行放入：

```
/sdcard/TouhouPet/
├── pet/       # 宠物动画
└── system/    # 系统资源与存档
```

旧版（原应用）的存档和动画、音乐数据可以直接使用，无需转换。

## 从源码构建

环境要求：JDK 17+、Android SDK（compileSdk 36）。

```bash
./gradlew assembleDebug   # 调试包 → app/build/outputs/apk/debug/
./gradlew assembleRelease # 发布包 → app/build/outputs/apk/release/
```

## 项目结构

```
app/src/main/
├── kotlin/    # 业务代码（Kotlin + 协程）
├── java/      # 存档相关的领域类（保持 Java，兼容旧存档格式）
├── res/       # 布局与图片资源
└── jniLibs/   # libencrypt.so（存档加解密，arm64-v8a / armeabi-v7a）
```

## 项目背景

- 原版 **TouhouPet v1.0.2**已无法在现代 Android 上运行。
- 本仓库的original分支从原 APK 逆向重建，**行为保真**：与原版逐方法比对一致（255/255），
  宠物逻辑、存档格式与加解密算法完全兼容旧存档。
- 在此基础上做现代化改造：Kotlin 迁移、协程架构、`TYPE_APPLICATION_OVERLAY`
  悬浮窗、前台服务（specialUse）、自适应图标、16KB 页对齐等。

## 致谢与声明

- 东方 Project 版权归 [上海爱丽丝幻乐团（ZUN）](https://www16.big.or.jp/~zun/) 所有。
- 原应用版权归原作者所有；本仓库为学习与技术研究目的的逆向重建，不包含原版 APK 与商业素材。如侵权请联系删除。
