package k.p.modern

import k.p.utils.EnvironmentUtil
import java.io.File
import java.io.FileWriter

/** 诊断探针(临时):宠物启动链路打点到数据目录 app.diag.log,真机复现后读取定位 */
object Diag {
    private var enabled = true

    fun log(msg: String) {
        if (!enabled) return
        try {
            val file = File(EnvironmentUtil.getMainPath() + "/system/log/app.log")
            file.parentFile?.mkdirs()
            val w = FileWriter(file, true)
            w.write(android.text.format.DateFormat.format("MM-dd HH:mm:ss", System.currentTimeMillis()).toString() + " [" + Thread.currentThread().name + "] " + msg + "\n")
            w.flush()
            w.close()
        } catch (e: Exception) {
        }
    }

    fun disable() {
        enabled = false
    }
}