#!/usr/bin/env python3
# 机械转换 LocationService.java → Kotlin,保证所有字符串字面量零改动
import re

src = open('/data/data/com.termux/files/home/TouhouPet/app/src/main/java/k/p/services/LocationService.java').read()

# 抠出 init() 方法体(从 public static void init() { 到对应的 })
m = re.search(r'public static void init\(\) \{(.*?)\n    \}\n\n    public static void registerLocation', src, re.S)
body = m.group(1)

lines = body.split('\n')

# 收集被引用的类 import 列表(从原文件的 imports 正则)
imports = re.findall(r'import (k\.p\.[\w.]+);', src)
import_lines = ['import ' + i.replace('k.p', 'k.p') for i in imports]

out = []
def emit(s):
    out.append(s)

emit('package k.p.services')
emit('')
for i in sorted(set(imports)):
    emit('import ' + i)
emit('import k.p.location.BaseLocation')
emit('import k.p.location.Location')
emit('')
emit('object LocationService {')
emit('    @JvmField')
emit('    var HOME: Location? = null')
emit('    @JvmField')
emit('    var locationList: MutableList<Location>? = null')
emit('    @JvmField')
emit('    var locationMap: MutableMap<String, Location>? = null')
emit('')
emit('    @JvmStatic')
emit('    fun init() {')
emit('        locationMap = HashMap()')
emit('        locationList = ArrayList()')

skip_empty = True
for raw in lines:
    line = raw.rstrip()
    s = line.strip()
    if not s:
        emit('')
        continue

    # 匿名 BaseLocation 创建
    m = re.match(r'Location (\w+) = new BaseLocation\("([^"]*)"\) \{', s)
    if m:
        emit(f'        val {m.group(1)}: Location = object : BaseLocation("{m.group(2)}") {{')
        continue
    m = re.match(r'HOME = new BaseLocation\("([^"]*)"\) \{', s)
    if m:
        emit(f'        HOME = object : BaseLocation("{m.group(1)}") {{')
        continue
    # 匿名 LocationAction
    if s == 'new BaseLocation.LocationAction() {':
        emit('                registerLocationAction(object : BaseLocation.LocationAction {')
        continue
    if re.match(r'registerLocationAction\(new BaseLocation\.LocationAction\(\) \{', s):
        emit('                registerLocationAction(object : BaseLocation.LocationAction {')
        continue
    # FindItemAction / FindNothingAction
    m = re.match(r'registerLocationAction\(new BaseLocation\.FindItemAction\("(.*)", (\w+)\.class, (\d+)\)\)', s)
    if m:
        emit('                registerLocationAction(BaseLocation.FindItemAction("{0}", {1}::class.java, {2}))'.format(m.group(1), m.group(2), m.group(3)))
        continue
    m = re.match(r'registerLocationAction\(new BaseLocation\.FindNothingAction\("(.*)", (\d+)\)\)', s)
    if m:
        emit('                registerLocationAction(BaseLocation.FindNothingAction("{0}", {1}))'.format(m.group(1), m.group(2)))
        continue
    # LocationAction 方法
    m = re.match(r'public void onTrigger\(BasePet (pet|basePet), Location ?(\w+)?\) \{', s)
    if m:
        emit('                    override fun onTrigger(pet: BasePet, location: Location?) {')
        continue
    m = re.match(r'public void onEnter\(\) \{', s)
    if m:
        emit('                    override fun onEnter() {')
        continue
    m = re.match(r'public void init\(\) \{', s)
    if m:
        emit('                    override fun init() {')
        continue
    m = re.match(r'public int getWeight\(\) \{ return (\d+); \}', s)
    if m:
        emit('                    override fun getWeight(): Int = {0}'.format(m.group(1)))
        continue
    m = re.match(r'public String getDoneMessage\(\) \{ return (.*?); \}', s)
    if m:
        emit('                    override fun getDoneMessage(): String = {0}'.format(m.group(1)))
        continue
    # 内部 Java 结构:new DialogService.CallBack() { → object : DialogService.CallBack {
    m = re.match(r'new DialogService\.CallBack\(\) \{', s)
    if m:
        emit('                                        object : DialogService.CallBack {')
        continue
    # DialogService.confirm( 5 参数 3 字符串... 手写参数区
    m = re.match(r'DialogService\.confirm\("(.*)", "(.*)", "(.*)", "(.*)", new DialogService\.CallBack\(\)', s)
    if m and False:
        pass
    # 保持其他行原样(可能需微调)
    emit(s)

emit('')
emit('        registerLocation(HOME!!)')
# locationList init 等结尾处理由外部 add
emit('    }')
emit('')
emit('    @JvmStatic')
emit('    fun registerLocation(location: Location) {')
emit('        locationMap!![location.getName()] = location')
emit('        locationList!!.add(location)')
emit('    }')
emit('')
emit('    @JvmStatic')
emit('    fun findLocationByName(locationName: String): Location? {')
emit('        return locationMap!![locationName]')
emit('    }')
emit('')
emit('    @JvmStatic')
emit('    fun release() {')
emit('        locationMap = null')
emit('        locationList = null')
emit('        HOME = null')
emit('    }')
emit('}')

open('/data/data/com.termux/files/home/TouhouPet/app/src/main/kotlin/k/p/services/LocationService.generated.kt', 'w').write('\n'.join(out))
print('generated')