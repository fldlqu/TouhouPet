package local.kcn.utils

object MathUtil {
    @JvmStatic
    fun getDistanceXY(ox: Float, oy: Float, tx: Float, ty: Float): Float =
        Math.sqrt((((ox - tx) * (ox - tx)) + ((oy - ty) * (oy - ty))).toDouble()).toFloat()

    @JvmStatic
    fun minAbs(a: Float, b: Float): Float = Math.min(Math.abs(a), Math.abs(b))

    @JvmStatic
    fun floatEquals(a: Float, b: Float): Boolean = Math.abs(a - b) < 1.0E-5f
}
