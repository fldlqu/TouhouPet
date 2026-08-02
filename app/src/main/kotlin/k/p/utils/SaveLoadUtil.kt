package k.p.utils

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.StreamCorruptedException

object SaveLoadUtil {
    private val SAVEPATH = EnvironmentUtil.getMainPath().toString() + "/system/save/"

    @JvmStatic
    external fun decipheringFile(str: String)

    @JvmStatic
    private external fun encryptFile(str: String)

    init {
        System.loadLibrary("encrypt")
    }

    /**
     * Reconstructed faithfully from the smali (the jadx output had variable
     * scope errors). Structure: try / catch (rethrow) / finally (close +
     * encrypt), matching the original bytecode exactly.
     */
    @JvmStatic
    @Throws(Exception::class)
    fun save(obj: Any?, fileName: String) {
        val savePath = File(SAVEPATH)
        if (!savePath.exists()) {
            savePath.mkdirs()
        }
        val file = File(SAVEPATH + fileName)
        if (!file.exists()) {
            file.createNewFile()
        }
        var out: ObjectOutputStream? = null
        try {
            out = ObjectOutputStream(FileOutputStream(file))
            out.writeObject(obj)
            out.flush()
        } catch (e: Exception) {
            throw e
        } finally {
            if (out != null) {
                out.close()
            }
            encryptFile(SAVEPATH + fileName)
        }
    }

    @JvmStatic
    @Throws(Exception::class)
    fun load(clazz: Class<*>, fileName: String): Any? {
        var obj: Any? = null
        val file = File(SAVEPATH + fileName)
        if (file.exists()) {
            decipheringFile(SAVEPATH + fileName)
            var `in`: ObjectInputStream
            try {
                `in` = ObjectInputStream(FileInputStream(file))
            } catch (sce: StreamCorruptedException) {
                decipheringFile(SAVEPATH + fileName)
                `in` = ObjectInputStream(FileInputStream(file))
            }
            obj = `in`.readObject()
            `in`.close()
        }
        return obj
    }

    @JvmStatic
    fun clear(fileName: String) {
        val file = File(SAVEPATH + fileName)
        if (file.exists()) {
            file.delete()
        }
    }
}