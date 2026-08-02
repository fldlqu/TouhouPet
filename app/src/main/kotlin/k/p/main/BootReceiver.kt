package k.p.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/* 开机自启:MainService 存活期间会写 boot_restore 标记;用户主动退出时清除。
 * 系统重启后若标记还在, 自动恢复桌面宠物。 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) {
            return
        }
        val prefs = context.getSharedPreferences("thp_prefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("boot_restore", false)) {
            try {
                context.startForegroundService(Intent(context, MainService::class.java))
            } catch (e: Exception) {
            }
        }
    }
}