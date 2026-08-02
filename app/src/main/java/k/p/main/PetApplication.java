package k.p.main;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import k.p.utils.EnvironmentUtil;

/* 现代化:统一初始化入口(数据目录、通知渠道)。原版无 Application 类。 */
public class PetApplication extends Application {
    public static final String CHANNEL_ID = "pet";

    @Override
    public void onCreate() {
        super.onCreate();
        EnvironmentUtil.init(this);
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "TouhouPet", NotificationManager.IMPORTANCE_LOW);
        channel.setDescription("桌面宠物状态");
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .createNotificationChannel(channel);
    }
}
