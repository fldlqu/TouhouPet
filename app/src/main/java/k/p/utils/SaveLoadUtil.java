package k.p.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

/* JADX INFO: loaded from: classes.dex */
public class SaveLoadUtil {
    private static final String SAVEPATH = String.valueOf(EnvironmentUtil.getMainPath()) + "/system/save/";

    private static native void decipheringFile(String str);

    private static native void encryptFile(String str);

    static {
        System.loadLibrary("encrypt");
    }

    /**
     * Reconstructed faithfully from the smali (the jadx output had variable
     * scope errors). Structure: try / catch (rethrow) / finally (close +
     * encrypt), matching the original bytecode exactly.
     */
    public static void save(Object obj, String fileName) throws Exception {
        File savePath = new File(SAVEPATH);
        if (!savePath.exists()) {
            savePath.mkdirs();
        }
        File file = new File(String.valueOf(SAVEPATH) + fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(obj);
            out.flush();
        } catch (Exception e) {
            throw e;
        } finally {
            if (out != null) {
                out.close();
            }
            encryptFile(String.valueOf(SAVEPATH) + fileName);
        }
    }

    public static Object load(Class<?> clazz, String fileName) throws Exception {
        Object obj = null;
        File file = new File(String.valueOf(SAVEPATH) + fileName);
        if (file.exists()) {
            decipheringFile(String.valueOf(SAVEPATH) + fileName);
            ObjectInputStream in;
            try {
                in = new ObjectInputStream(new FileInputStream(file));
            } catch (StreamCorruptedException sce) {
                decipheringFile(String.valueOf(SAVEPATH) + fileName);
                in = new ObjectInputStream(new FileInputStream(file));
            }
            obj = in.readObject();
            in.close();
        }
        return obj;
    }

    public static void clear(String fileName) {
        File file = new File(String.valueOf(SAVEPATH) + fileName);
        if (file.exists()) {
            file.delete();
        }
    }
}
