package local.kcn.utils;

/* JADX INFO: loaded from: classes.dex */
public class MathUtil {
    public static float getDistanceXY(float ox, float oy, float tx, float ty) {
        // FloatMath.sqrt removed in API 23; equivalent to (float) Math.sqrt
        return (float) Math.sqrt(((ox - tx) * (ox - tx)) + ((oy - ty) * (oy - ty)));
    }

    public static float minAbs(float a, float b) {
        return Math.min(Math.abs(a), Math.abs(b));
    }

    public static boolean floatEquals(float a, float b) {
        return Math.abs(a - b) < 1.0E-5f;
    }
}
