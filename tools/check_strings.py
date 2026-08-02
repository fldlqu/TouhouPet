#!/usr/bin/env python3
# 校验: Java 原文与 Kotlin 稿的字符串字面量集合必须一致(保真)
import re, sys

def decode(s):
    """解码 Java/Kotlin 字符串转义,得到运行时字符串"""
    out = []
    i = 0
    while i < len(s):
        c = s[i]
        if c == '\\' and i + 1 < len(s):
            n = s[i + 1]
            if n == '\\':
                out.append('\\')
                i += 2
                continue
            if n == '"':
                out.append('"')
                i += 2
                continue
            if n == 'n':
                out.append('\n')
                i += 2
                continue
            if n == 'r':
                out.append('\r')
                i += 2
                continue
            if n == 't':
                out.append('\t')
                i += 2
                continue
        out.append(c)
        i += 1
    return ''.join(out)


def extract(path):
    s = open(path).read()
    # 简单提取所有 "..." 字面量并解码转义
    strs = re.findall(r'"((?:[^"\\]|\\.)*)"', s)
    return [decode(x) for x in strs]

j = extract(sys.argv[1])
k = extract(sys.argv[2])

from collections import Counter
cj, ck = Counter(j), Counter(k)
missing = cj - ck
extra = ck - cj
if not missing and not extra:
    print('OK: 字符串字面量集合一致(%d 条)' % len(set(j)))
else:
    print('DIFF: Java 有但 Kotlin 缺:')
    for s, n in missing.items():
        print('  -', repr(s), 'x%d' % n)
    print('Kotlin 有但 Java 无:')
    for s, n in extra.items():
        print('  +', repr(s), 'x%d' % n)