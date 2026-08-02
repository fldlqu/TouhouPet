package k.p.modern

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** 通知栏"开发者"按钮 → 打开测试面板 */
class DevReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        DevPanel.show(context)
    }
}