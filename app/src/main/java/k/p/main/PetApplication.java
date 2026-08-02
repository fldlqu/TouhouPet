package k.p.main;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import k.p.utils.EnvironmentUtil;

/* 现代化:统一初始化入口(数据目录、通知渠道)。原版无 Application 类。 */
public class PetApplication extends Application {
    public static final String CHANNEL_ID = "pet";
    private Thread.UncaughtExceptionHandler defaultHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        EnvironmentUtil.init(this);
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "TouhouPet", NotificationManager.IMPORTANCE_LOW);
        channel.setDescription("桌面宠物状态");
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .createNotificationChannel(channel);
        installCrashHandler();
    }

    /* 崩溃兜底:未捕获异常写入 crash.log(诊断用,不影响原版行为) */
    private void installCrashHandler() {
        final Thread.UncaughtExceptionHandler prev =
                Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                try {
                    File dir = getFilesDir();
                    if (dir != null) {
                        FileWriter w = new FileWriter(new File(dir, "crash.log"), true);
                        w.write(new Date().toString() + "\n"
                                + Log.getStackTraceString(throwable) + "\n\n");
                        w.close();
                    }
                } catch (IOException ignored) {
                }
                if (prev != null) {
                    prev.uncaughtException(thread, throwable);
                }
            }
        });
    }
}
