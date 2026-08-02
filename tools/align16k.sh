#!/data/data/com.termux/files/usr/bin/bash
# Android 16 (16KB 页)兼容:对 APK 做 16KB 对齐 + 重签(debug key)。
# extractNativeLibs=false 时 .so 从 zip 直接映射,16KB 页设备要求 zip 内
# .so 条目 offset/size 均为 16384 的倍数。AGP 不自动做,构建后跑本脚本:
#   ./tools/align16k.sh
set -e
BT="$HOME/android-sdk/build-tools/36.0.0"
KS="$HOME/.android/debug.keystore"
cd "$(dirname "$0")/.."

for APK in app/build/outputs/apk/debug/app-debug.apk app/build/outputs/apk/release/app-release.apk; do
    [ -f "$APK" ] || continue
    OUT="${APK%.apk}-16k.apk"
    TMP="${OUT}.tmp"
    "$BT/zipalign" -P 16 -f 4 "$APK" "$TMP"
    "$BT/apksigner" sign --ks "$KS" --ks-pass pass:android --out "$OUT" "$TMP"
    rm -f "$TMP"
    echo "16KB aligned: $OUT"
done
