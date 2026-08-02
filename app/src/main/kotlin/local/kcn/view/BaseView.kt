package local.kcn.view

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.AttributeSet
import android.util.TypedValue
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
 * 与旧 BaseSurfaceView 的差异(仅渲染驱动层):
 * - SurfaceView(独立 Surface / 后台线程 lockCanvas) → View(onDraw 主线程绘制)
 * - 表面尺寸: SurfaceView 由系统决定 → View 由 WindowManager 布局尺寸决定
 *   (悬浮窗经 DesktopService 挂载, params.width/height 即绘制尺寸)
 * - 表面生命周期: onAttachedToWindow/onDetachedFromWindow 替代
 *   surfaceCreated/surfaceDestroyed, 不可见即暂停可见即恢复
 *
 * 保持不变的业务语义:
 * - MAX_FPS 40 上限 + updateInterval 节拍(Handler 帧调度)
 * - paused / requestPause / requestStop / startDraw 全部保留
 * - updateStatus(time) 返回 true 才真正 invalidate 重绘(与旧版"返回值决定 lockCanvas"一致)
 * - loadBitmap/releaseBitmap 资源链与 bitmapList 回收完全保留
 * - protected 字段/方法签名不变, Kotlin 子类无需改动
 *
 * 状态机(与旧 BaseSurfaceView 等价):
 * - startDraw() 仅是"请求开始"标志(addView 场景下由 DesktopService 调用)
 * - onAttachedToWindow 时若请求过且未暂停 → 启动帧循环
 * - onDetachedFromWindow → 停帧 + paused=true(对应 surfaceDestroyed -- requestPause)
 * - requestPause 停帧; 下次 attach 恢复
 * - requestStop 终止并释放资源
 */
open class BaseView : View {
    companion object {
        @JvmField
        val MAX_FPS = 40.0f

        @JvmField
        val MIN_FPS = 0.01f

        @JvmField
        val TAG = "BaseView"
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

    /** 异步初始化作用域(构造时启动) */
    private val asyncScope = AppScopes.newDefault()

    /** 帧调度(主线程) */
    private val frameHandler = Handler(Looper.getMainLooper())
    private val frameRunnable = Runnable { onFrameTick() }
    private var frameScheduled = false
    private var attached = false
    private var frameRequested = false
    private var stopped = false

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        paused = false
        loop = true
        updateStatusWhenPaused = false
        frames = 0
        offset = 0L
        res = context.resources
        /* 渲染语义同旧 SurfaceView(软件画布): 透明背景 + 每帧位移完整重绘。
         * 硬件加速 Canvas 不支持 PorterDuff.CLEAR(子类 update 里 clearPaint),
         * 也用层保证透明度/混合与旧版一致。 */
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        init0()
        setWillNotDraw(false)
        asyncScope.launch {
            asyncInit()
            asyncInitCompleted()
        }
    }

    /** 请求开始绘制(原 startDraw + holder.addCallback)。attach 前调用只记标志。 */
    fun startDraw() {
        frameRequested = true
        currentTime = SystemClock.elapsedRealtime()
        startTime = currentTime
        lastTime = currentTime
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
                paused = false /* 原 surfaceCreated 语义: attach 恢复 */
            }
            if (frameRequested && !paused) {
                startFrameLoop()
            }
        }
    }

    override fun onDetachedFromWindow() {
        attached = false
        stopFrameLoop()
        if (!paused && !stopped) {
            paused = true /* 原 surfaceDestroyed 语义: 不可见即暂停 */
        }
        super.onDetachedFromWindow()
    }

    private fun startFrameLoop() {
        if (frameScheduled || !loop || !attached) {
            return
        }
        frameScheduled = true
        frameHandler.post(frameRunnable)
    }

    private fun stopFrameLoop() {
        frameScheduled = false
        frameHandler.removeCallbacks(frameRunnable)
    }

    /** 一帧节拍: 时间推进 + updateStatus + 可选 invalidate(对应旧 loop 前半段) */
    private fun onFrameTick() {
        frameScheduled = false
        if (!loop || !attached || paused) {
            return
        }
        val elapsed = SystemClock.elapsedRealtime()
        /* 与旧 loop 一致: lastTime 为上一帧时刻, time 为帧间隔 */
        val time = (elapsed - lastTime).toInt()
        lastTime = elapsed
        currentTime = elapsed
        frames++
        offset = elapsed - startTime
        if (updateStatus(time)) {
            invalidate()
        }
        /* 节拍控制: 到下一帧的最小等待时间 = 帧耗用后的剩余间隔 */
        val minTime = (updateInterval - time).toInt()
        if (frameScheduled || !loop || !attached || paused) {
            return
        }
        frameScheduled = true
        if (minTime > 0) {
            frameHandler.postDelayed(frameRunnable, minTime.toLong())
        } else {
            frameHandler.post(frameRunnable)
        }
    }

    override fun onDraw(canvas: Canvas) {
        /* release0()已回收位图; 若系统因布局/移动再次重绘, 直接跳过防 RecycledBitmap 崩溃 */
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