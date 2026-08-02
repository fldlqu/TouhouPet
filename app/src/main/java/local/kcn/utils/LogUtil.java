package local.kcn.utils;

import android.util.Log;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class LogUtil {
    private static List<ExceptionListener> listenerList = new ArrayList();
    public static boolean debug = false;
    public static boolean record = true;
    public static String recordPath = null;

    public interface ExceptionListener {
        void occurException(Exception exc);
    }

    public static void log(String tag, String msg) {
        if (debug) {
            Log.e(tag, msg);
        }
    }

    public static void log(String msg) {
        log("LOG", msg);
    }

    public static void log(Exception e) {
        log("EXCEPTION", e);
    }

    public static void log(Exception e, boolean canCatch) {
        log("EXCEPTION", e, canCatch);
    }

    public static void log(String tag, Exception e) {
        log(tag, e, true);
    }

    public static void log(String tag, Exception e, boolean canCatch) {
        FileWriter fw = null;
        if (canCatch) {
            for (ExceptionListener listener : listenerList) {
                listener.occurException(e);
            }
        }
        log(tag, "----------------Exception----------------");
        log(tag, Log.getStackTraceString(e));
        log(tag, "-----------------------------------------");
        if (record && recordPath != null) {
            FileWriter fw2 = null;
            File file = null;
            File file2 = new File(recordPath);
            try {
                if (file2.exists()) {
                    file = file2;
                } else {
                    file2.createNewFile();
                    file = file2;
                }
            } catch (IOException e2) {
                file = file2;
            }
            try {
                fw = new FileWriter(file, true);
            } catch (IOException e4) {
            } catch (Throwable th) {
            }
            try {
                fw.write(String.valueOf(new Date().toString()) + "\r\n");
                fw.write("----------------Exception----------------\r\n");
                fw.write("TAG = " + tag + "\r\n");
                fw.write(String.valueOf(Log.getStackTraceString(e)) + "\r\n");
                fw.write("-----------------------------------------\r\n\r\n");
                fw.flush();
                if (fw != null) {
                    try {
                        fw.close();
                    } catch (IOException e5) {
                    }
                }
            } catch (IOException e6) {
                fw2 = fw;
                if (fw2 != null) {
                    try {
                        fw2.close();
                    } catch (IOException e7) {
                    }
                }
            } catch (Throwable th2) {
                fw2 = fw;
                if (fw2 != null) {
                    try {
                        fw2.close();
                    } catch (IOException e8) {
                    }
                }
                if (th2 instanceof RuntimeException) {
                    throw (RuntimeException) th2;
                }
                if (th2 instanceof Error) {
                    throw (Error) th2;
                }
                throw new RuntimeException(th2);
            }
        }
    }

    public static void registerExceptionListener(ExceptionListener listener) {
        listenerList.add(listener);
    }

    public static void clearExceptionListeners() {
        listenerList.clear();
    }
}
