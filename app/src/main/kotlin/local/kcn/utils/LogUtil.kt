package local.kcn.utils

import android.util.Log
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.ArrayList
import java.util.Date

object LogUtil {
    private val listenerList = ArrayList<ExceptionListener>()
    @JvmField
    var debug = false
    @JvmField
    var record = true
    @JvmField
    var recordPath: String? = null

    fun interface ExceptionListener {
        fun occurException(exc: Exception?)
    }

    @JvmStatic
    fun log(tag: String, msg: String) {
        if (debug) {
            Log.e(tag, msg)
        }
    }

    @JvmStatic
    fun log(msg: String) {
        log("LOG", msg)
    }

    @JvmStatic
    fun log(e: Exception) {
        log("EXCEPTION", e)
    }

    @JvmStatic
    fun log(e: Exception, canCatch: Boolean) {
        log("EXCEPTION", e, canCatch)
    }

    @JvmStatic
    fun log(tag: String, e: Exception) {
        log(tag, e, true)
    }

    @JvmStatic
    fun log(tag: String, e: Exception, canCatch: Boolean) {
        if (canCatch) {
            for (listener in listenerList) {
                listener.occurException(e)
            }
        }
        log(tag, "----------------Exception----------------")
        log(tag, Log.getStackTraceString(e))
        log(tag, "-----------------------------------------")
        if (record && recordPath != null) {
            var file = File(recordPath)
            try {
                if (!file.exists()) {
                    file.createNewFile()
                }
            } catch (e2: IOException) {
            }
            var fw: FileWriter? = null
            try {
                fw = FileWriter(file, true)
            } catch (e3: Exception) {
            }
            if (fw != null) {
                try {
                    fw.write(Date().toString() + "\r\n")
                    fw.write("----------------Exception----------------\r\n")
                    fw.write("TAG = " + tag + "\r\n")
                    fw.write(Log.getStackTraceString(e) + "\r\n")
                    fw.write("-----------------------------------------\r\n\r\n")
                    fw.flush()
                    fw.close()
                } catch (e4: Exception) {
                }
            }
        }
    }

    @JvmStatic
    fun registerExceptionListener(listener: ExceptionListener) {
        listenerList.add(listener)
    }

    @JvmStatic
    fun clearExceptionListeners() {
        listenerList.clear()
    }
}