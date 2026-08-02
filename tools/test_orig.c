/*
 * 在原版 libencrypt.so(armeabi)上运行黑盒测试的 32 位入口版。
 * 无 crt:自己提供 _start,避免 libc 初始化依赖;输出用 write 系统调用。
 */
#include <dlfcn.h>
#include <signal.h>
#include <stdint.h>
#include <stdlib.h>
#include <unistd.h>

typedef void *jobject;

void *load_so(const char *path, const char *sym); /* mini_loader.c */

static void wr(const char *s) {
    int n = 0;
    while (s[n]) n++;
    write(1, s, n);
}

static void sys_exit(int code) { _exit(code); }

static void segv(int sig, siginfo_t *si, void *ctx) {
    (void)sig; (void)ctx;
    char buf[64];
    int n = 0;
    buf[n++] = 'S'; buf[n++] = 'E'; buf[n++] = 'G'; buf[n++] = 'V';
    buf[n++] = ' '; buf[n++] = '0'; buf[n++] = 'x';
    uintptr_t a = (uintptr_t)si->si_addr;
    for (int sh = 28; sh >= 0; sh -= 4) {
        int d = (int)((a >> sh) & 0xf);
        buf[n++] = (char)(d < 10 ? '0' + d : 'a' + d - 10);
    }
    buf[n++] = '\n';
    write(1, buf, n);
    _exit(1);
}

static const char *fakeGet(void **e, jobject s, void *c) {
    (void)e; (void)c;
    return (const char *)s;
}
static void fakeRel(void **e, jobject s, const char *c) {
    (void)e; (void)s; (void)c;
}

static void fail(const char *m) { wr(m); sys_exit(1); }

int run(void) {
    struct sigaction sa;
    sa.sa_sigaction = segv;
    sa.sa_flags = SA_SIGINFO;
    sigemptyset(&sa.sa_mask);
    sigaction(SIGSEGV, &sa, NULL);

    FILE *f = fopen("test.bin", "wb");
    if (!f) fail("create failed\n");
    for (int i = 0; i < 3000; i++) fputc((i * 131 + 7) & 0xff, f);
    fclose(f);

    void **table = calloc(256, sizeof(void *));
    if (!table) fail("calloc failed\n");
    table[169] = (void *)fakeGet;
    table[170] = (void *)fakeRel;
    void **env = malloc(sizeof(void *));
    if (!env) fail("malloc failed\n");
    *env = (void *)table; /* JNIEnv 是指向函数表指针的指针 */

    void *h = load_so("./libencrypt-orig.so", "Java_k_p_utils_SaveLoadUtil_encryptFile");
    if (!h) fail("load_so failed\n");
    void (*ef)(void *, void *, jobject) = (void (*)(void *, void *, jobject))h;

    ef(env, NULL, (jobject)"test.bin");

    FILE *g = fopen("test.bin", "rb");
    if (!g) fail("test.bin missing after encrypt\n");
    int ok = 1;
    for (int i = 0; i < 3000; i++) {
        int c = fgetc(g);
        if (c != (((i * 131 + 7) & 0xff) ^ 0x0c)) {
            wr("mismatch in encrypted content\n");
            ok = 0;
            break;
        }
    }
    fclose(g);
    FILE *b = fopen("test.bin.buf", "rb");
    if (b) { wr("buf file left behind\n"); fclose(b); ok = 0; }
    if (ok) wr("ORIG SO == XOR-0x0C ALGO: VERIFIED\n");
    return ok ? 0 : 1;
}

void _start(void) {
    int r = run();
    sys_exit(r);
}
