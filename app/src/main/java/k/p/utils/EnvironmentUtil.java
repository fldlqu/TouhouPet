package k.p.utils;

import android.os.Environment;

/* JADX INFO: loaded from: classes.dex */
public class EnvironmentUtil {
    private static String mainPath = Environment.getExternalStorageDirectory() + "/TouhouPet";

    public static String getMainPath() {
        return mainPath;
    }
}
