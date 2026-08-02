#!/data/data/com.termux/files/usr/bin/env bash
# 编译 libencrypt.c 为 arm64-v8a(新编译)与 armeabi(仅用于本地对比测试,不替换原版)
# 原版 armeabi/libencrypt.so 保持逐字节不变(行为保真);
# arm64-v8a 由本脚本从反汇编还原的 C 源码编译。
set -euo pipefail
cd "$(dirname "$0")/.."
SRC=app/src/main/jni/libencrypt.c
OUT=app/src/main/jniLibs

mkdir -p "$OUT/arm64-v8a"
clang -target aarch64-linux-android -O2 -fPIC -shared -o "$OUT/arm64-v8a/libencrypt.so" "$SRC"
echo "-> $OUT/arm64-v8a/libencrypt.so"
readelf -h "$OUT/arm64-v8a/libencrypt.so" | grep -E "Class|Machine"

# 可选:同时编译 armeabi-v7a 做本地对比(不放入 APK 的 armeabi 目录)
if [ "${1:-}" = "all" ]; then
    mkdir -p build/encrypt-test
    clang -target armv7a-linux-androideabi -march=armv7-a -mthumb -O2 -fPIC -shared -nostdlib \
        -o build/encrypt-test/libencrypt-armv7.so "$SRC"
    echo "-> build/encrypt-test/libencrypt-armv7.so"
    readelf -h build/encrypt-test/libencrypt-armv7.so | grep -E "Class|Machine"
fi
