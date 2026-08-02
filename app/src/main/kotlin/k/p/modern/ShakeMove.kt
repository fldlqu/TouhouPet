package k.p.modern

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import k.p.services.ViewService
import kotlin.math.sqrt

/**
 * 摇晃移动:手机摇晃时宠物沿摇晃方向移动一步。
 * 设定菜单开关控制(thp_prefs "shake_move")。
 *
 * 传感器:优先 TYPE_LINEAR_ACCELERATION(系统内置高通滤波分离重力,
 * 直接给出摇晃加速度, 无自己滤波吃掉高频信号的问题); 设备不支持时
 * 降级为 TYPE_ACCELEROMETER + 慢低通滤波(alpha=0.95, 时间常数~400ms,
 * 不衰减 2~3Hz 的晃动信号)。
 *
 * 合成幅度超过阈值(~0.8g)判定摇晃; 水平分量(x/y)即方向: 宠物沿该方向
 * 移动屏宽/4, 边界内。x 同号、y 取反(传感器 y 向上 vs 屏幕 y 向下)。
 * 触发后冷却 600ms 防连发。回调在主线程, 与原版 onDrag 改位置同线程语义。
 */
object ShakeMove : SensorEventListener {
    private const val SHAKE_THRESHOLD = 8f
    private const val COOLDOWN_MS = 600L
    private const val GRAVITY_ALPHA = 0.95f

    private var sensorManager: SensorManager? = null
    private var registered = false
    private var lastShakeTime = 0L
    private var useLinearAccel = false

    /** 加速度计降级路径:低通滤波估计重力(alpha=0.95, 慢, 不吞晃动信号) */
    private val gravity = floatArrayOf(0f, 0f, 0f)
    private var firstSample = true

    /** 开关切换:开启注册传感器,关闭注销;返回是否成功注册 */
    fun setEnabled(context: Context, enabled: Boolean): Boolean {
        return if (enabled) {
            register(context)
        } else {
            unregister()
            true
        }
    }

    private fun register(context: Context): Boolean {
        if (registered) {
            return true
        }
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager ?: return false
        val linearAccel = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        val accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val sensor = linearAccel ?: accelerometer ?: return false
        useLinearAccel = sensor.type == Sensor.TYPE_LINEAR_ACCELERATION
        /* SENSOR_DELAY_GAME(~20ms): 摇晃峰值持续时间短, UI 档(60ms)会错过 */
        if (!sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)) {
            return false
        }
        sensorManager = sm
        registered = true
        firstSample = true
        return true
    }

    private fun unregister() {
        if (!registered) {
            return
        }
        sensorManager?.unregisterListener(this)
        sensorManager = null
        registered = false
    }

    /** MainService.onDestroy 时调用,确保监听器注销 */
    fun release() {
        unregister()
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_LINEAR_ACCELERATION &&
            event.sensor.type != Sensor.TYPE_ACCELEROMETER
        ) {
            return
        }
        val ax: Float
        val ay: Float
        val az: Float
        if (useLinearAccel) {
            ax = event.values[0]
            ay = event.values[1]
            az = event.values[2]
        } else {
            /* 降级路径:慢低通滤波分离重力 */
            if (firstSample) {
                gravity[0] = event.values[0]
                gravity[1] = event.values[1]
                gravity[2] = event.values[2]
                firstSample = false
                return
            }
            gravity[0] = GRAVITY_ALPHA * gravity[0] + (1 - GRAVITY_ALPHA) * event.values[0]
            gravity[1] = GRAVITY_ALPHA * gravity[1] + (1 - GRAVITY_ALPHA) * event.values[1]
            gravity[2] = GRAVITY_ALPHA * gravity[2] + (1 - GRAVITY_ALPHA) * event.values[2]
            ax = event.values[0] - gravity[0]
            ay = event.values[1] - gravity[1]
            az = event.values[2] - gravity[2]
        }
        val magnitude = sqrt(ax * ax + ay * ay + az * az)
        if (magnitude < SHAKE_THRESHOLD) {
            return
        }
        val now = System.currentTimeMillis()
        if (now - lastShakeTime < COOLDOWN_MS) {
            return
        }
        lastShakeTime = now
        /* 方向取水平分量(x/y);平放纯 z 摇晃时水平分量近零,不移动 */
        ViewService.petView?.moveByDirection(ax, ay)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}
