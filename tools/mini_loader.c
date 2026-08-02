/*
 * mini_loader.c — 迷你 ELF32 loader,在 arm64 设备的 32 位兼容模式下
 * 加载 2012 年编译的 libencrypt-orig.so(TEXTREL,现代 bionic linker 拒绝)。
 * 手动 mmap + 重定位,等价于 Android 8 之前 / targetSdk<23 的豁免行为。
 *
 * 支持:PT_LOAD 映射、RELATIVE/ABS32/GLOB_DAT/JUMP_SLOT 重定位、
 * 外部符号经 dlsym(RTLD_DEFAULT) 解析。仅测试用途。
 */
#include <dlfcn.h>
#include <elf.h>
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <unistd.h>

static int nchain = 0; /* 由 load_so 设置 */

static void *dep_handles[16];
static int ndeps = 0;

static Elf32_Addr resolve(Elf32_Sym *symtab, const char *strtab, Elf32_Addr base,
                          const char *name) {
    for (int i = 0; i < nchain; i++) {
        Elf32_Sym *s = &symtab[i];
        const char *nm = strtab + s->st_name;
        if (strcmp(nm, name) == 0) {
            if (s->st_shndx != SHN_UNDEF) return base + s->st_value;
            /* 未定义:先查 DT_NEEDED 依赖,再 RTLD_DEFAULT */
            for (int d = 0; d < ndeps; d++) {
                void *r = dlsym(dep_handles[d], name);
                if (r) return (Elf32_Addr)r;
            }
            return (Elf32_Addr)dlsym(RTLD_DEFAULT, name);
        }
    }
    return (Elf32_Addr)dlsym(RTLD_DEFAULT, name);
}

void *load_so(const char *path, const char *sym) {
    int fd = open(path, O_RDONLY);
    if (fd < 0) return NULL;
    struct stat st;
    fstat(fd, &st);
    size_t fsz = st.st_size;
    unsigned char *file = mmap(NULL, fsz, PROT_READ, MAP_PRIVATE, fd, 0);
    close(fd);
    if (file == MAP_FAILED) return NULL;

    Elf32_Ehdr *eh = (Elf32_Ehdr *)file;
    if (memcmp(eh->e_ident, ELFMAG, 4) || eh->e_ident[EI_CLASS] != ELFCLASS32) {
        munmap(file, fsz);
        return NULL;
    }

    Elf32_Phdr *ph = (Elf32_Phdr *)(file + eh->e_phoff);
    size_t max_vaddr = 0;
    for (int i = 0; i < eh->e_phnum; i++)
        if (ph[i].p_type == PT_LOAD) {
            size_t end = ph[i].p_vaddr + ph[i].p_memsz;
            if (end > max_vaddr) max_vaddr = end;
        }
    size_t total = (max_vaddr + 0xfff) & ~0xfffUL;
    /* RWX:含 text reloc,与旧豁免行为一致 */
    unsigned char *base = mmap(NULL, total, PROT_READ | PROT_WRITE | PROT_EXEC,
                               MAP_PRIVATE | MAP_ANONYMOUS, -1, 0);
    if (base == MAP_FAILED) { munmap(file, fsz); return NULL; }
    for (int i = 0; i < eh->e_phnum; i++) {
        if (ph[i].p_type != PT_LOAD) continue;
        memcpy(base + ph[i].p_vaddr, file + ph[i].p_offset, ph[i].p_filesz);
        if (ph[i].p_memsz > ph[i].p_filesz)
            memset(base + ph[i].p_vaddr + ph[i].p_filesz, 0, ph[i].p_memsz - ph[i].p_filesz);
    }
    /* 动态段 vaddr 必须在 munmap 前从 ph 读取(ph 指向 file) */
    Elf32_Addr dyn_vaddr = 0;
    for (int i = 0; i < eh->e_phnum; i++)
        if (ph[i].p_type == PT_DYNAMIC) dyn_vaddr = ph[i].p_vaddr;
    munmap(file, fsz);

    Elf32_Dyn *dyn = (Elf32_Dyn *)(base + dyn_vaddr);
    Elf32_Sym *symtab = NULL;
    const char *strtab = NULL;
    Elf32_Rel *rel = NULL, *jmprel = NULL;
    size_t relsz = 0, jmprelsz = 0;
    int pltrel = DT_REL;
    unsigned needed_offs[16];
    int nneed = 0;
    for (Elf32_Dyn *d = dyn; d->d_tag != DT_NULL; d++)
        if (d->d_tag == DT_NEEDED && nneed < 16) needed_offs[nneed++] = d->d_un.d_val;
    for (Elf32_Dyn *d = dyn; d->d_tag != DT_NULL; d++) {
        switch (d->d_tag) {
        case DT_HASH: {
            unsigned *h = (unsigned *)(base + d->d_un.d_ptr);
            nchain = h[1];
                    break;
        }
        case DT_SYMTAB: symtab = (Elf32_Sym *)(base + d->d_un.d_ptr); break;
        case DT_STRTAB: strtab = (const char *)(base + d->d_un.d_ptr); break;
        case DT_REL: rel = (Elf32_Rel *)(base + d->d_un.d_ptr); break;
        case DT_RELSZ: relsz = d->d_un.d_val; break;
        case DT_JMPREL: jmprel = (Elf32_Rel *)(base + d->d_un.d_ptr); break;
        case DT_PLTRELSZ: jmprelsz = d->d_un.d_val; break;
        case DT_PLTREL: pltrel = d->d_un.d_val; break;
        }
    }
    if (!symtab || !strtab) return NULL;
    /* 加载 DT_NEEDED 依赖(strtab 已就绪) */
    for (int i = 0; i < nneed; i++) {
        const char *lib = strtab + needed_offs[i];
        dep_handles[ndeps] = dlopen(lib, RTLD_NOW | RTLD_GLOBAL);
        if (!dep_handles[ndeps]) {
            char full[64];
            snprintf(full, sizeof(full), "/system/lib/%s", lib);
            dep_handles[ndeps] = dlopen(full, RTLD_NOW | RTLD_GLOBAL);
        }
        if (dep_handles[ndeps]) ndeps++;
    }
    Elf32_Addr base_addr = (Elf32_Addr)base;

    #define APPLY(r, n) do { \
        for (size_t i = 0; i < (n); i++) { \
            Elf32_Addr *where = (Elf32_Addr *)(base_addr + (r)[i].r_offset); \
            unsigned t = ELF32_R_TYPE((r)[i].r_info); \
            Elf32_Addr S = 0; \
            unsigned symidx = ELF32_R_SYM((r)[i].r_info); \
            if (symidx) { \
                Elf32_Sym *s = &symtab[symidx]; \
                S = resolve(symtab, strtab, base_addr, strtab + s->st_name); \
            } \
            switch (t) { \
            case R_ARM_RELATIVE:  *where = base_addr + *where; break; \
            case R_ARM_ABS32:     *where = S + *where; break; \
            case R_ARM_GLOB_DAT:  *where = S; break; \
            case R_ARM_JUMP_SLOT: *where = S; break; \
            default: fprintf(stderr, "unsupported reloc type %u\n", t); return NULL; \
            } \
        } \
    } while (0)
    APPLY(rel, relsz / sizeof(Elf32_Rel));
    if (pltrel == DT_REL) APPLY(jmprel, jmprelsz / sizeof(Elf32_Rel));

    return (void *)resolve(symtab, strtab, base_addr, sym);
}
