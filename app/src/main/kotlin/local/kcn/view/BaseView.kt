package local.kcn.view

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.SystemClock
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Choreographer
import android.view.View
import android.view.WindowManager
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import local.kcn.utils.LogUtil
import k.p.modern.AppScopes
import k.p.modern.Diag
import java.io.FileDescriptor
import java.io.InputStream
import java.util.ArrayList

/**
 * 渲染基座: 普通 View(onDraw) 替代原 SurfaceView + lockCanvas 自绘体系。
 *
 * 换基座的原因: SurfaceView 的独立 Surface 与锁屏线程, 同悬浮窗叠加、窗口合成、
 * 现代动画体系的交互摩擦大; 普通 View 由系统 ViewRoot 统一合成, 兼容性好。
 *
 * 对子类保持不变的契约(改动需谨慎):
 * - updateStatus(time) 返回 true 才 invalidate → 决定真重绘(旧版返回值决定 lockCanvas)
 * - requestPause / requestStop / startDraw 语义
 * - loadBitmap/releaseBitmap 资源链与 bitmapList 回收
 * - protected 字段/签名(子类零改动)
 *
 * 渲染后端: 硬件加速(不设软件层)。子类 update 用 clearPaint(CLEAR) 清屏,
 * 该模式在 hardware canvas 受支持, 悬浮窗透明无需软件离屏层。
 *
 * 帧调度与线程模型:
 * - 帧节拍走主线程 + Choreographer vsync(非后台线程 lockCanvas), 见 onFrameTick
 * - 生命周期: attach 时若请求过且未暂停 → 启动帧循环; detach → 停帧 + paused
 */
open class BaseView : View {
    companion object {
        /** 帧率上限与目标档位: 设定可切换"跟随系统(系统刷新率)/40/60",
         * 所有 view 的 setCurrentFPS 都被 clamp 到此值, 档位变更即全局统一。 */
        @JvmField
        var MAX_FPS = 40.0f

        @JvmField
        val MIN_FPS = 0.01f

        @JvmField
        val TAG = "BaseView"

        /* 为什么不直接用 ViewService: 档位须覆盖不在 ViewService 里的窗口视图
         * (如 MainView)。构造注册, detach 注销。 */
        private val activeViews = java.util.concurrent.CopyOnWriteArrayList<BaseView>()

        @JvmStatic
        fun applyMaxFpsToActive() {
            for (view in activeViews) {
                view.setCurrentFPS(MAX_FPS)
            }
        }
    }

    @JvmField
    protected var SCREEN_HEIGHT = 0

    @JvmField
    protected var SCREEN_WIDTH = 0

    @JvmField
    protected var X_SCALE = 0f

    @JvmField
    protected var Y_SCALE = 0f

    private var bitmapList: MutableList<Bitmap?> = ArrayList()

    @JvmField
    protected var currentFPS = 0f

    @JvmField
    protected var currentTime = 0L

    @JvmField
    protected var frames = 0

    @JvmField
    protected var lastTime = 0L

    @JvmField
    protected var loop = true

    @JvmField
    protected var offset = 0L

    @JvmField
    protected var paused = false

    @JvmField
    protected var picPaint: Paint? = null

    @JvmField
    protected var res: Resources? = null

    @JvmField
    protected var startTime = 0L

    @JvmField
    protected var updateInterval = 0

    @JvmField
    protected var updateStatusWhenPaused = false

    private val asyncScope = AppScopes.newDefault()

    private val frameChoreographer = Choreographer.getInstance()
    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            onFrameTick()
        }
    }
    private var frameScheduled = false
    private var attached = false
    private var frameRequested = false
    private var stopped = false
    /** 累加式到期基准: 推帧后 + interval, 见 onFrameTick */
    private var lastFrameDue = 0L

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        paused = false
        loop = true
        updateStatusWhenPaused = false
        frames = 0
        offset = 0L
        res = context.resources
        init0()
        setWillNotDraw(false)
        /* 帧率档位必须先于 UI 挂载生效(启动时 applyFrameRateCeiling 在
         * addView 之前执行), 所以构造即注册而非 attach 时注册。 */
        activeViews.add(this)
        asyncScope.launch {
            asyncInit()
            asyncInitCompleted()
        }
    }

    /** 请求开始绘制: attach 前调用只记标志, 挂载后才真正启动帧循环 */
    fun startDraw() {
        frameRequested = true
        currentTime = SystemClock.elapsedRealtime()
        startTime = currentTime
        lastTime = currentTime
        lastFrameDue = currentTime
        frameScheduled = false
        if (attached && !paused) {
            startFrameLoop()
        }
    }

    fun requestStop() {
        Diag.log("requestStop " + this.javaClass.simpleName)
        stopped = true
        loop = false
        stopFrameLoop()
        asyncScope.cancel()
        release0()
    }

    open fun requestPause() {
        paused = true
        stopFrameLoop()
    }

    protected open fun init() {
    }

    protected open fun asyncInit() {
    }

    protected open fun asyncInitCompleted() {
    }

    protected open fun updateStatus(time: Int): Boolean = true

    protected open fun update(canvas: Canvas) {
    }

    protected open fun release() {
    }

    private fun init0() {
        setCurrentFPS(MAX_FPS)
        SCREEN_WIDTH = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            .defaultDisplay.width
        SCREEN_HEIGHT = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            .defaultDisplay.height
        X_SCALE = SCREEN_WIDTH / 720.0f
        Y_SCALE = SCREEN_HEIGHT / 1280.0f
        picPaint = Paint()
        bitmapList = ArrayList()
        init()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        attached = true
        if (!stopped) {
            if (paused) {
                paused = false /* attach 即恢复(原 surfaceCreated) */
            }
            if (frameRequested && !paused) {
                startFrameLoop()
            }
        }
    }

    override fun onDetachedFromWindow() {
        activeViews.remove(this)
        attached = false
        stopFrameLoop()
        if (!paused && !stopped) {
            paused = true /* 不可见即暂停(原 surfaceDestroyed) */
        }
        super.onDetachedFromWindow()
    }

    private fun startFrameLoop() {
        if (frameScheduled || !loop || !attached) {
            return
        }
        frameScheduled = true
        frameChoreographer.postFrameCallback(frameCallback)
    }

    private fun stopFrameLoop() {
        frameScheduled = false
        frameChoreographer.removeFrameCallback(frameCallback)
    }

    private fun onFrameTick() {
        if (frameScheduled) {
            frameScheduled = false
        }
        if (!loop || !attached || paused) {
            return
        }
        val elapsed = SystemClock.elapsedRealtime()
        /* vsync 对齐帧频: 用累加式到期基准(lastFrameDue += interval)而非
         * 简单比较 elapsed - lastTime, 后者误差随时间累积、帧间隔漂移。 */
        val intervalMs = updateInterval.coerceAtLeast(1).toLong()
        if (elapsed - lastFrameDue < intervalMs) {
            scheduleNext() /* 未到期: 下个 vsync 再来 */
            return
        }
        val time = (elapsed - lastFrameDue).toInt()
        lastTime = elapsed
        currentTime = elapsed
        frames++
        offset = elapsed - startTime
        lastFrameDue += intervalMs
        if (elapsed - lastFrameDue >= intervalMs) {
            /* 绘制耗时越过一个间隔: 基准跳到当前, 防追赶式连发帧风暴 */
            lastFrameDue = elapsed
        }
        if (updateStatus(time)) {
            invalidate()
        }
        scheduleNext()
    }

    private fun scheduleNext() {
        if (!frameScheduled && loop && attached && !paused) {
            frameScheduled = true
            frameChoreographer.postFrameCallback(frameCallback)
        }
    }

    override fun onDraw(canvas: Canvas) {
        /* 位图已随 release0() 回收: 系统因布局/移动触发的重绘直接跳过, 否则 RecycledBitmap 崩溃 */
        if (!loop) {
            return
        }
        super.onDraw(canvas)
        update(canvas)
    }

    private fun release0() {
        for (bitmap in bitmapList) {
            if (bitmap != null && !bitmap.isRecycled) {
                bitmap.recycle()
            }
        }
        bitmapList.clear()
        release()
    }

    protected fun loadBitmap(data: ByteArray, offset: Int, length: Int): Bitmap? {
        val bitmap = BitmapFactory.decodeByteArray(data, offset, length)
        bitmapList.add(bitmap)
        return bitmap
    }

    protected fun loadBitmap(data: ByteArray, offset: Int, length: Int, opts: BitmapFactory.Options): Bitmap? {
        val bitmap = BitmapFactory.decodeByteArray(data, offset, length, opts)
        bitmapList.add(bitmap)
        return bitmap
    }

    protected fun loadBitmap(pathName: String): Bitmap? {
        val bitmap = BitmapFactory.decodeFile(pathName)
        bitmapList.add(bitmap)
        return bitmap
    }

    protected fun loadBitmap(pathName: String, opts: BitmapFactory.Options): Bitmap? {
        val bitmap = BitmapFactory.decodeFile(pathName, opts)
        bitmapList.add(bitmap)
        return bitmap
    }

    protected fun loadBitmap(fd: FileDescriptor): Bitmap? {
        val bitmap = BitmapFactory.decodeFileDescriptor(fd)
        bitmapList.add(bitmap)
        return bitmap
    }

    protected fun loadBitmap(fd: FileDescriptor, outPadding: Rect?, opts: BitmapFactory.Options): Bitmap? {
        val bitmap = BitmapFactory.decodeFileDescriptor(fd, outPadding, opts)
        bitmapList.add(bitmap)
        return bitmap
    }

    protected fun loadBitmap(id: Int): Bitmap? {
        val bitmap = BitmapFactory.decodeResource(res, id)
        bitmapList.add(bitmap)
        return bitmap
    }

    protected fun loadBitmap(id: Int, opts: BitmapFactory.Options): Bitmap? {
        val bitmap = BitmapFactory.decodeResource(res, id, opts)
        bitmapList.add(bitmap)
        return bitmap
    }

    protected fun loadBitmap(value: TypedValue, stream: InputStream, pad: Rect?, opts: BitmapFactory.Options): Bitmap? {
        val bitmap = BitmapFactory.decodeResourceStream(res, value, stream, pad, opts)
        bitmapList.add(bitmap)
        return bitmap
    }

    protected fun loadBitmap(stream: InputStream): Bitmap? {
        val bitmap = BitmapFactory.decodeStream(stream)
        bitmapList.add(bitmap)
        return bitmap
    }

    protected fun loadBitmap(stream: InputStream, outPadding: Rect?, opts: BitmapFactory.Options): Bitmap? {
        val bitmap = BitmapFactory.decodeStream(stream, outPadding, opts)
        bitmapList.add(bitmap)
        return bitmap
    }

    protected fun releaseBitmap(bitmap: Bitmap?) {
        if (bitmap != null) {
            if (!bitmap.isRecycled) {
                bitmap.recycle()
            }
            bitmapList.remove(bitmap)
        }
    }

    protected fun log(msg: String) {
        LogUtil.log(msg)
    }

    protected fun log(tag: String, msg: String) {
        LogUtil.log(tag, msg)
    }

    protected fun log(e: Exception) {
        LogUtil.log(e)
    }

    fun setDebug(debug: Boolean) {
        LogUtil.debug = debug
    }

    fun getCurrentFPS(): Float = currentFPS

    fun isPaused(): Boolean = paused

    fun setCurrentFPS(currentFPS: Float) {
        var fps = currentFPS
        if (fps > MAX_FPS) {
            fps = MAX_FPS
        } else if (fps < MIN_FPS) {
            fps = MIN_FPS
        }
        this.currentFPS = fps
        updateInterval = (1000.0f / fps).toInt()
    }
}