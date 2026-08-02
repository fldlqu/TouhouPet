package k.p.utils;

import android.content.Context;
import android.os.Environment;
import java.io.File;

/* 数据目录:与原版一致,公共 SD 卡根目录 /sdcard/TouhouPet。
 * Android 11+ 读写公共目录需要"所有文件访问"权限(由 TouhouPet 引导授权);
 * Android 10 及以下需要 WRITE_EXTERNAL_STORAGE。卸载不丢数据。 */
public class EnvironmentUtil {
    private static final String DIR_NAME = "TouhouPet";
    private static String mainPath = null;

    public static void init(Context context) {
        if (mainPath == null) {
            mainPath = new File(Environment.getExternalStorageDirectory(), DIR_NAME)
                    .getAbsolutePath();
        }
    }

    public static String getMainPath() {
        return mainPath;
    }
}
