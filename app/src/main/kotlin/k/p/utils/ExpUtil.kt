package k.p.utils

/** 等级经验曲线:sqrt(level)*5 + level + 1 */
object ExpUtil {
    @JvmStatic
    fun getLevelExp(level: Int): Int = (Math.sqrt(level.toDouble()).toInt() * 5) + level + 1
}
