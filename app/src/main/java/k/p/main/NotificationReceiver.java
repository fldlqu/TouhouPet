package k.p.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import k.p.services.ViewService;
import local.kcn.utils.LogUtil;

/* JADX INFO: loaded from: classes.dex */
public class NotificationReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (ViewService.petView == null) {
            context.startService(new Intent(context, (Class<?>) MainService.class));
            return;
        }
        if (ViewService.petView.isPaused()) {
            try {
                ViewService.petView.show();
                return;
            } catch (Exception e) {
                LogUtil.log("show petView fail");
                context.startService(new Intent(context, (Class<?>) MainService.class));
                return;
            }
        }
        try {
            ViewService.petView.hide();
        } catch (Exception e2) {
            LogUtil.log("hide petView fail");
        }
    }
}
