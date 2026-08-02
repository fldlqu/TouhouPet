package k.p.modern

import k.p.utils.EnvironmentUtil
import java.io.File
import java.io.FileWriter

/** 诊断日志: 打点数据目录 app.log, 真机定位用 */
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