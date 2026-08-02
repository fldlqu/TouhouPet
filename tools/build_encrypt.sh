#!/data/data/com.termux/files/usr/bin/env bash
# 编译 libencrypt.c 为 arm64-v8a 与 armeabi-v7a(替换原版 armeabi)。
# 背景:原版 armeabi so 带 TEXTREL,targetSdk>=23 时被现代 linker 拒绝;
# 重新编译(行为等价,算法逐字节一致)后 32 位设备可正常加载。
# original 分支保留原版文件。arm64 需 16KB 对齐(zip 层用 tools/align16k.sh)。
set -euo pipefail
cd "$(dirname "$0")/.."
SRC=app/src/main/jni/libencrypt.c
OUT=app/src/main/jniLibs

# arm64-v8a
mkdir -p "$OUT/arm64-v8a"
clang -target aarch64-linux-android -O2 -fPIC -shared -o "$OUT/arm64-v8a/libencrypt.so" "$SRC"
echo "-> $OUT/arm64-v8a/libencrypt.so"
readelf -h "$OUT/arm64-v8a/libencrypt.so" | grep -E "Class|Machine"

# armeabi-v7a(替换原版 armeabi;mold 不支持交叉,须 -nostdlib)
mkdir -p "$OUT/armeabi-v7a"
clang -target armv7a-linux-androideabi -march=armv7-a -mthumb -O2 -fPIC -shared -nostdlib \
    -o "$OUT/armeabi-v7a/libencrypt.so" "$SRC"
echo "-> $OUT/armeabi-v7a/libencrypt.so"
readelf -h "$OUT/armeabi-v7a/libencrypt.so" | grep -E "Class|Machine"

# 移除原版 armeabi(带 TEXTREL,现代 linker 拒绝)
rm -rf "$OUT/armeabi"
echo "removed armeabi/ (original TEXTREL so)"
