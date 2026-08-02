package k.p.main

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import k.p.utils.EnvironmentUtil
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.Date

/* 现代化:统一初始化入口(数据目录、通知渠道)。原版无 Application 类。 */
class PetApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        EnvironmentUtil.init(this)
        val channel = NotificationChannel(
            CHANNEL_ID, "TouhouPet", NotificationManager.IMPORTANCE_LOW
        )
        channel.description = "桌面宠物状态"
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        installCrashHandler()
    }

    /* 崩溃兜底:未捕获异常写入 crash.log(诊断用,不影响原版行为)。
     * 优先写公共数据目录(可被外部读取调诊),失败回退内部目录。 */
    private fun installCrashHandler() {
        val prev = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val stack = Date().toString() + "\nthread=" + thread.name +
                "\n" + Log.getStackTraceString(throwable) + "\n\n"
            var primary: File? = null
            try {
                primary = File(EnvironmentUtil.getMainPath() + "/system/log", "crash.log")
            } catch (ignored: Exception) {
            }
            val candidates = arrayOf(primary, File(filesDir, "crash.log"))
            for (f in candidates) {
                if (f == null) {
                    continue
                }
                try {
                    val parent = f.parentFile
                    if (parent != null) {
                        parent.mkdirs()
                    }
                    val w = FileWriter(f, true)
                    w.write(stack)
                    w.close()
                    break
                } catch (_: IOException) {
                }
            }
            prev?.uncaughtException(thread, throwable)
        }
    }

    companion object {
        const val CHANNEL_ID = "pet"
    }
}