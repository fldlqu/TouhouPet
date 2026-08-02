package k.p.utils;

import android.content.Context;
import android.os.Environment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* 现代化:数据目录从公共 SD 卡(/sdcard/TouhouPet)迁至应用专属外部目录
 * (getExternalFilesDir,无需存储权限,卸载时删除)。
 * 首次启动自动迁移旧版 SD 卡数据。 */
public class EnvironmentUtil {
    private static final String DIR_NAME = "TouhouPet";
    private static String mainPath = null;

    public static void init(Context context) {
        if (mainPath != null) {
            return;
        }
        File dir = context.getExternalFilesDir(null);
        mainPath = new File(dir != null ? dir : context.getFilesDir(), DIR_NAME).getAbsolutePath();
    }

    public static String getMainPath() {
        return mainPath;
    }

    /* 旧版数据位置:SD 卡根目录 TouhouPet */
    public static File getLegacyPath() {
        return new File(Environment.getExternalStorageDirectory(), DIR_NAME);
    }

    /* 首次运行:把旧版 SD 卡数据整个拷到新目录。返回 true 表示执行了迁移。 */
    public static boolean migrateFromLegacy(Context context) {
        File legacy = getLegacyPath();
        File target = new File(getMainPath());
        if (!legacy.isDirectory() || target.exists()) {
            return false;
        }
        try {
            copyRecursive(legacy, target);
            return true;
        } catch (IOException e) {
            // 迁移失败不阻塞启动,新目录可能不完整,由调用方决定如何处理
            return false;
        }
    }

    private static void copyRecursive(File from, File to) throws IOException {
        if (from.isDirectory()) {
            if (!to.exists() && !to.mkdirs()) {
                throw new IOException("mkdir failed: " + to);
            }
            File[] children = from.listFiles();
            if (children == null) {
                return;
            }
            for (File child : children) {
                copyRecursive(child, new File(to, child.getName()));
            }
        } else {
            copyFile(from, to);
        }
    }

    private static void copyFile(File from, File to) throws IOException {
        File parent = to.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("mkdir failed: " + parent);
        }
        InputStream in = new FileInputStream(from);
        try {
            OutputStream out = new FileOutputStream(to);
            try {
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) > 0) {
                    out.write(buf, 0, n);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }
}
