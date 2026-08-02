package k.p.utils;

/* JADX INFO: loaded from: classes.dex */
public class ExpUtil {
    public static int getLevelExp(int level) {
        return (((int) Math.sqrt(level)) * 5) + level + 1;
    }
}
