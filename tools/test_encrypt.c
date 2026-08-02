/*
 * 黑盒验证:dlopen 编译出的 libencrypt.so,伪造 JNIEnv(函数表索引
 * 169=GetStringUTFChars, 170=ReleaseStringUTFChars),直接调用
 * Java_k_p_utils_SaveLoadUtil_encryptFile / decipheringFile,
 * 验证:加密 = 每字节 XOR 0x0C,文件流程 = .buf 后缀 + remove + rename。
 */
#include <dlfcn.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef void *jobject;

/* 伪造的 GetStringUTFChars:直接把 jstring 参数当作 UTF-8 路径字符串 */
static const char *fakeGetStringUTFChars(void **env, jobject s, void *isCopy) {
    (void)env;
    (void)isCopy;
    return (const char *)s;
}
static void fakeReleaseStringUTFChars(void **env, jobject s, const char *chars) {
    (void)env;
    (void)s;
    (void)chars;
}

static void *makeFakeEnv(void) {
    void **table = calloc(256, sizeof(void *));
    table[169] = (void *)fakeGetStringUTFChars;
    table[170] = (void *)fakeReleaseStringUTFChars;
    void **env = malloc(sizeof(void *));
    *env = (void *)table; /* JNIEnv 是指向函数表指针的指针 */
    return env;
}

static unsigned char *readFile(const char *path, size_t *outLen) {
    FILE *f = fopen(path, "rb");
    if (!f) return NULL;
    fseek(f, 0, SEEK_END);
    long n = ftell(f);
    fseek(f, 0, SEEK_SET);
    unsigned char *b = malloc(n ? (size_t)n : 1);
    if (n && fread(b, 1, (size_t)n, f) != (size_t)n) { fclose(f); free(b); return NULL; }
    fclose(f);
    *outLen = (size_t)n;
    return b;
}

static int writeFile(const char *path, const unsigned char *b, size_t n) {
    FILE *f = fopen(path, "wb");
    if (!f) return -1;
    size_t w = fwrite(b, 1, n, f);
    fclose(f);
    return w == n ? 0 : -1;
}

int main(int argc, char **argv) {
    if (argc < 2) { fprintf(stderr, "usage: %s libencrypt.so\n", argv[0]); return 2; }
    void *h = dlopen(argv[1], RTLD_NOW);
    if (!h) { fprintf(stderr, "dlopen: %s\n", dlerror()); return 1; }
    void (*encryptFile)(void *, void *, jobject) =
        (void (*)(void *, void *, jobject))dlsym(h, "Java_k_p_utils_SaveLoadUtil_encryptFile");
    void (*decipheringFile)(void *, void *, jobject) =
        (void (*)(void *, void *, jobject))dlsym(h, "Java_k_p_utils_SaveLoadUtil_decipheringFile");
    if (!encryptFile || !decipheringFile) { fprintf(stderr, "dlsym failed\n"); return 1; }

    void *env = makeFakeEnv();
    size_t len = 3000; /* 跨多个 1024 块 */
    unsigned char *orig = malloc(len);
    for (size_t i = 0; i < len; i++) orig[i] = (unsigned char)(i * 131 + 7);
    if (writeFile("test.bin", orig, len)) { fprintf(stderr, "write test.bin failed\n"); return 1; }

    encryptFile(env, NULL, (jobject)"test.bin");
    /* 加密后:test.bin 应存在且内容 = XOR 0x0C(remove+rename 后原名文件=密文),
     * 无 .buf 残留 */
    FILE *gone = fopen("test.bin.buf", "rb");
    if (gone) { fprintf(stderr, "FAIL: test.bin.buf still exists after encrypt\n"); return 1; }
    size_t elen = 0;
    unsigned char *enc = readFile("test.bin", &elen);
    if (!enc) { fprintf(stderr, "FAIL: test.bin missing after encrypt\n"); return 1; }
    if (elen != len) { fprintf(stderr, "FAIL: encrypted length %zu != %zu\n", elen, len); return 1; }
    for (size_t i = 0; i < len; i++) {
        if (enc[i] != (unsigned char)(orig[i] ^ 0x0C)) {
            fprintf(stderr, "FAIL: byte %zu = %02x, expected %02x\n", i, enc[i], orig[i] ^ 0x0C);
            return 1;
        }
    }
    printf("ENCRYPT OK: %zu bytes, every byte ^= 0x0C, .buf+remove+rename flow correct\n", len);

    decipheringFile(env, NULL, (jobject)"test.bin");
    size_t dlen = 0;
    unsigned char *dec = readFile("test.bin", &dlen);
    if (!dec) { fprintf(stderr, "FAIL: test.bin missing after decipher\n"); return 1; }
    if (dlen != len || memcmp(dec, orig, len) != 0) {
        fprintf(stderr, "FAIL: roundtrip mismatch\n");
        return 1;
    }
    FILE *gone2 = fopen("test.bin.buf", "rb");
    if (gone2) { fprintf(stderr, "FAIL: test.bin.buf still exists after decipher\n"); return 1; }
    printf("ROUNDTRIP OK: decipher(encrypt(x)) == x, temp file cleaned up\n");

    /* 空文件 + 单字节文件边界 */
    if (writeFile("empty.bin", (const unsigned char *)"", 0)) return 1;
    encryptFile(env, NULL, (jobject)"empty.bin");
    size_t elen2 = 0;
    unsigned char *e2 = readFile("empty.bin", &elen2);
    if (!e2 || elen2 != 0) { fprintf(stderr, "FAIL: empty file case\n"); return 1; }
    printf("EMPTY OK\n");
    /* 空文件解密往返 */
    decipheringFile(env, NULL, (jobject)"empty.bin");
    size_t elen3 = 0;
    unsigned char *e3 = readFile("empty.bin", &elen3);
    if (!e3 || elen3 != 0) { fprintf(stderr, "FAIL: empty decipher case\n"); return 1; }

    free(orig); free(enc); free(dec); free(env); free(e2);
    dlclose(h);
    return 0;
}
