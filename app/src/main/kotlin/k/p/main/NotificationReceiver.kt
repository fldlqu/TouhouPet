package k.p.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import k.p.services.ViewService
import local.kcn.utils.LogUtil

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (ViewService.petView == null) {
            context.startService(Intent(context, MainService::class.java))
            return
        }
        if (ViewService.petView!!.isPaused()) {
            try {
                ViewService.petView!!.show()
                return
            } catch (e: Exception) {
                LogUtil.log("show petView fail")
                context.startService(Intent(context, MainService::class.java))
                return
            }
        }
        try {
            ViewService.petView!!.hide()
        } catch (e: Exception) {
            LogUtil.log("hide petView fail")
        }
    }
}