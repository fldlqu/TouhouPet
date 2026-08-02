package k.p.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper

/* 开机自启:MainService 存活期间会写 boot_restore 标记;用户主动退出时清除。
 * 系统重启后若标记还在, 自动恢复桌面宠物。
 * 延迟 10 秒启动:开机广播到达时系统窗口服务尚未完全就绪,
 * 立即 addView 悬浮窗在部分系统上会 attach 崩溃。 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) {
            return
        }
        val prefs = context.getSharedPreferences("thp_prefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("boot_restore", false)) {
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    context.startForegroundService(Intent(context, MainService::class.java))
                } catch (e: Exception) {
                }
            }, 10000L)
        }
    }
}