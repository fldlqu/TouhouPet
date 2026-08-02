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
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import local.kcn.utils.LogUtil
import k.p.modern.AppScopes
import k.p.modern.Diag
import java.io.FileDescriptor
import java.io.InputStream
import java.util.ArrayList

/**
 * 架构现代化:Kotlin 重写 + 协程绘制循环(替代 2012 年的裸 Thread)。
 *
 * 行为语义与原版一致:
 * - surfaceCreated 时启动绘制循环(原版 drawThread.start())
 * - 40 FPS 上限、paused 语义、updateStatus 返回 false 时不重绘
 * - requestStop 用协程取消(比原版 join(1000) 更干净)
 *
 * 全部 protected 成员用 @JvmField/open 保持与 Java 子类(PetView/MainView/…)
 * 的二进制兼容:字段直接访问、方法覆写签名不变。
 */
open class BaseSurfaceView : SurfaceView, SurfaceHolder.Callback {

    companion object {
        @JvmField
        val MAX_FPS = 40.0f

        @JvmField
        val MIN_FPS = 0.01f

        @JvmField
        val TAG = "BaseSurfaceView"
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

    /** 绘制循环作用域(surfaceCreated 时启动一次);单线程保证 lock/unlock 同线程 */
    private val drawScope = AppScopes.newSingle()

    /** 异步初始化作用域(构造时启动) */
    private val asyncScope = AppScopes.newSingle()

    private var drawStarted = false
    private var firstFrameLogged = false

    /** 绘制协程 Job(requestStop 时等待其退出后再回收资源) */
    private var drawJob: Job? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        paused = false
        loop = true
        updateStatusWhenPaused = false
        frames = 0
        offset = 0L
        res = context.resources
        holder.setFormat(-3)
        init0()
        asyncScope.launch {
            asyncInit()
            asyncInitCompleted()
        }
    }

    fun startDraw() {
        holder.addCallback(this)
    }

    fun requestStop() {
        Diag.log("requestStop " + this.javaClass.simpleName)
        loop = false
        // 协程取消:绘制循环在 delay 挂起点退出(对应原版 join(1000))
        drawScope.cancel()
        asyncScope.cancel()
        // 等绘制协程真正结束再回收 bitmap,避免回收正在绘制的帧 → Canvas: recycled bitmap 崩溃
        // (原版语义:join 后 finally 里 release0;绘制协程在 Default 线程,阻塞等待无死锁)
        if (drawJob != null) {
            runBlocking {
                drawJob?.join()
            }
        }
        release0()
    }

    open fun requestPause() {
        paused = true
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

    private fun updateStatus0(): Boolean {
        val time = (currentTime - lastTime).toInt()
        frames++
        offset = currentTime - startTime
        return updateStatus(time)
    }

    private fun update0(canvas: Canvas) {
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

    /** 绘制循环:与原版 loop() 语义等价。
     *  关键修正:lockCanvas/unlockCanvasAndPost 必须同线程配对(ReentrantLock),
     *  delay 是挂起点会换线程 → unlock 移到 delay 之前,lock→unlock 间无挂起点。
     *  顺序:draw(lock→update→unlock)→ delay → 下一轮,首帧立即绘制,节拍同原版。 */
    private suspend fun loop() {
        currentTime = SystemClock.elapsedRealtime()
        startTime = currentTime
        while (coroutineContext.isActive && loop) {
            var canvas: Canvas? = null
            try {
                if (!paused || updateStatusWhenPaused) {
                    lastTime = currentTime
                    currentTime = SystemClock.elapsedRealtime()
                    if (updateStatus0()) {
                        synchronized(holder) {
                            canvas = holder.lockCanvas()
                            if (canvas != null && !paused) {
                                try {
                                    update0(canvas)
                                } catch (e: Exception) {
                                    LogUtil.log(TAG, e)
                                }
                            }
                        }
                    }
                }
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)
                    if (!firstFrameLogged) {
                        firstFrameLogged = true
                        Diag.log(" + frame " + this.javaClass.simpleName + " " + canvas.width + "x" + canvas.height)
                    }
                }
                val minTime = (updateInterval - (currentTime - lastTime)).toInt()
                if (minTime > 0) {
                    delay(minTime.toLong())
                }
            } catch (t: Throwable) {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)
                }
                if (t is CancellationException) {
                    throw t
                }
                throw t
            }
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Diag.log("surfaceCreated " + this.javaClass.simpleName + " paused=" + paused + " drawStarted=" + drawStarted + " scopeActive=" + drawScope.isActive)
        if (paused) {
            paused = false
        } else if (!drawStarted) {
            drawStarted = true
            drawJob = drawScope.launch {
                Diag.log("draw loop start " + this.javaClass.simpleName)
                loop()
            }
        } else {
            Diag.log("surfaceCreated SKIP: drawStarted already true")
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        if (!paused) {
            requestPause()
        }
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
        val bitmap: Bitmap? = BitmapFactory.decodeStream(stream)
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
