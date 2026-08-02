/*
 * libencrypt.c — 从原版 libencrypt.so (armeabi, ELF32 ARM / Thumb-2) 反汇编
 * 逐条还原的等价实现。
 *
 * 原版导出两个 JNI 函数(均跳转到同一实现 _Z11encryptFileP7_JNIEnvP8_jstring):
 *   Java_k_p_utils_SaveLoadUtil_encryptFile      (JNIEnv*, jclass, jstring)
 *   Java_k_p_utils_SaveLoadUtil_decipheringFile  (JNIEnv*, jclass, jstring)
 *
 * 还原出的算法(反汇编 + 常量池佐证):
 *   1. GetStringUTFChars 取得文件路径
 *   2. fopen(原名, "rb")
 *   3. 新文件名 = 原名 + ".buf"(5 字节,含 '\0')
 *   4. fopen(新名, "wb")
 *   5. 循环: fread(buf, 1, 1024, fin) → 每字节 ^= 0x0C → fwrite
 *   6. fclose 两文件; remove(原名); rename(新名 → 原名)
 *   7. ReleaseStringUTFChars
 * XOR 对称,encrypt == deciphering。
 *
 * 不依赖 jni.h / NDK:JNI 函数表索引是 ABI 规范
 * (GetStringUTFChars = 169, ReleaseStringUTFChars = 170)。
 */
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef void *jobject;

/* JNI C 绑定:env 是指向函数表指针的指针,须先 *env 再索引。
 * 原版反汇编佐证:ldr r2,[r0]; ldr r3,[r2,#0x2a4](先解引用)。 */
static const char *getStringUTFChars(void *env, jobject s, void *isCopy) {
    void *const *table = *(void *const **)env;
    return ((const char *(*)(void *, jobject, void *))table[169])(env, s, isCopy);
}

static void releaseStringUTFChars(void *env, jobject s, const char *chars) {
    void *const *table = *(void *const **)env;
    ((void (*)(void *, jobject, const char *))table[170])(env, s, chars);
}

static void encryptFile0(void *env, jobject path) {
    if (path == NULL) return; /* 原版: jstring == 0 直接返回 */
    const char *name = getStringUTFChars(env, path, NULL);
    if (name == NULL) return;
    FILE *fin = fopen(name, "rb");
    if (fin == NULL) return; /* 原版此处无检查(文件不存在会崩);防御性返回 */
    size_t len = strlen(name);
    char *newname = (char *)malloc(len + 5);
    if (newname == NULL) {
        fclose(fin);
        return;
    }
    memcpy(newname, name, len + 1);
    memcpy(newname + len, ".buf", 5); /* 追加 ".buf\0" */
    FILE *fout = fopen(newname, "wb");
    if (fout == NULL) {
        fclose(fin);
        free(newname);
        return;
    }
    char buf[1024];
    size_t n;
    while (!feof(fin)) { /* 原版按 FILE._flags 的 EOF 位循环,语义同 feof */
        n = fread(buf, 1, sizeof(buf), fin);
        if (n > 0) {
            for (size_t i = 0; i < n; i++) buf[i] ^= 0x0C; /* 常量池 0x0c0c */
            fwrite(buf, 1, n, fout);
        }
    }
    fclose(fin);
    fclose(fout);
    remove(name);
    rename(newname, name);
    free(newname);
    releaseStringUTFChars(env, path, name);
}

void Java_k_p_utils_SaveLoadUtil_encryptFile(void *env, void *clazz, jobject path) {
    (void)clazz;
    encryptFile0(env, path);
}

void Java_k_p_utils_SaveLoadUtil_decipheringFile(void *env, void *clazz, jobject path) {
    (void)clazz;
    encryptFile0(env, path);
}
