package k.p.view

import android.content.Context
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.Toast
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import k.p.modern.AppScopes
import k.p.services.DesktopService
import local.kcn.utils.MathUtil
import local.kcn.view.BaseSurfaceView

/**
 * 架构现代化:Kotlin 重写。
 * - java.util.Timer 长按判断 → 协程 delay + Job 取消
 * - show/hide 的匿名 Thread → 协程(后台 scope)
 * - Handler.sendEmptyMessage(WM 刷新排队) → 主线程协程 launch
 * 行为语义与原版一致;@JvmField/open 保持 Java 子类兼容。
 */
open class BaseDesktopView : BaseSurfaceView {

    companion object {
        private const val CLICK_DISTANCE_RANGE = 30.0f
        private const val CLICK_TIME_RANGE = 500
        private const val LONGCLICK_TIME_RANGE = 2000L
    }

    @JvmField
    protected var MOVETOTARGET_MINSPEED = 2

    private var applicationContext: Context? = null

    @JvmField
    protected var clearPaint: Paint? = null

    private var downTime = 0L
    private var downX = 0f
    private var downY = 0f
    private var dragLastTime = 0L
    private var dragLastX = 0f
    private var dragLastY = 0f
    private var dragStartTime = 0L
    private var dragStartX = 0f
    private var dragStartY = 0f

    @JvmField
    protected var isLongClick = false

    /** 长按判断协程(替代 Timer.schedule + LongClickJudgeTask.cancel) */
    private var longClickJob: Job? = null

    @JvmField
    protected var moveToTargetBufferedSpeed = 1.0f

    @JvmField
    protected var params: WindowManager.LayoutParams? = null

    @JvmField
    protected var viewCurrentX = 0f

    @JvmField
    protected var viewCurrentY = 0f

    @JvmField
    protected var viewHeight = 0

    @JvmField
    protected var viewTargetX = 0f

    @JvmField
    protected var viewTargetY = 0f

    @JvmField
    protected var viewWidth = 0

    /** 触摸/动画作用域(主线程:长按判断、WM 刷新) */
    private val mainScope = AppScopes.newMain()

    /** 后台 show/hide 异步动作 */
    private val actionScope = AppScopes.newDefault()

    constructor(context: Context) : super(context) {
        moveToTargetBufferedSpeed = 1.0f
        MOVETOTARGET_MINSPEED = 2
        isLongClick = false
        applicationContext = context.applicationContext
    }

    fun toast(text: String) {
        toast(text, 0)
    }

    fun toast(text: String, duration: Int) {
        Toast.makeText(applicationContext, text, duration).show()
    }

    protected open fun onClick(x: Float, y: Float) {
    }

    protected open fun onLongClick(x: Float, y: Float) {
    }

    protected open fun onTouch(event: MotionEvent) {
    }

    protected open fun onDown(x: Float, y: Float) {
    }

    protected open fun onMove(x: Float, y: Float, time: Int) {
    }

    protected open fun onUp(x: Float, y: Float) {
    }

    protected open fun onDrag(x: Float, y: Float, time: Int) {
    }

    open fun hide() {
        onHide()
        DesktopService.removeDesktopView(this)
        actionScope.launch {
            onHideAsync()
        }
    }

    open fun show() {
        onShow()
        DesktopService.addDesktopView(this, params!!)
        actionScope.launch {
            onShowAsync()
        }
    }

    protected open fun onShow() {
    }

    protected open fun onHide() {
    }

    protected open fun onShowAsync() {
    }

    protected open fun onHideAsync() {
    }

    override fun updateStatus(time: Int): Boolean {
        val distance = Math.abs(viewTargetX - viewCurrentX)
        viewCurrentX = if (distance > MOVETOTARGET_MINSPEED) {
            if (viewTargetX > viewCurrentX) {
                ((distance / 5.0f) * moveToTargetBufferedSpeed + MOVETOTARGET_MINSPEED).toInt() + viewCurrentX
            } else {
                ((-distance) / 10.0f - MOVETOTARGET_MINSPEED).toInt() + viewCurrentX
            }
        } else {
            viewTargetX
        }
        val distance2 = Math.abs(viewTargetY - viewCurrentY)
        viewCurrentY = if (distance2 > MOVETOTARGET_MINSPEED) {
            if (viewTargetY > viewCurrentY) {
                ((distance2 / 5.0f) * moveToTargetBufferedSpeed + MOVETOTARGET_MINSPEED).toInt() + viewCurrentY
            } else {
                ((-distance2) / 10.0f - MOVETOTARGET_MINSPEED).toInt() + viewCurrentY
            }
        } else {
            viewTargetY
        }
        val p = params
        if (p != null && (p.x != viewCurrentX.toInt() || p.y != viewCurrentY.toInt())) {
            p.x = viewCurrentX.toInt()
            p.y = viewCurrentY.toInt()
            mainScope.launch {
                DesktopService.refreshDesktopView(this@BaseDesktopView)
            }
        }
        return true
    }

    override fun init() {
        params = WindowManager.LayoutParams().apply {
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            flags = 776
            gravity = 51
            x = 0
            y = 0
        }
        viewHeight = 50
        viewWidth = 50
        params!!.width = getViewWidth()
        params!!.height = getViewHeight()
        clearPaint = Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val eventTime = event.eventTime
        val eventX = event.rawX
        val eventY = event.rawY
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isLongClick = false
                longClickJob = mainScope.launch {
                    delay(LONGCLICK_TIME_RANGE)
                    if (MathUtil.getDistanceXY(eventX, eventY, dragLastX, dragLastY) < CLICK_DISTANCE_RANGE) {
                        isLongClick = true
                        onLongClick(eventX, eventY)
                    }
                }
                downTime = eventTime
                downX = eventX
                downY = eventY
                dragLastTime = eventTime
                dragLastX = eventX
                dragLastY = eventY
                dragStartTime = eventTime
                dragStartX = eventX
                dragStartY = eventY
                onDown(eventX, eventY)
                onTouch(event)
                return true
            }

            MotionEvent.ACTION_UP -> {
                onUp(eventX, eventY)
                longClickJob?.cancel()
                if (!isLongClick) {
                    val touchTime = (eventTime - downTime).toInt()
                    if (touchTime < CLICK_TIME_RANGE &&
                        MathUtil.getDistanceXY(eventX, eventY, downX, downY) < CLICK_DISTANCE_RANGE
                    ) {
                        onClick(eventX, eventY)
                    } else {
                        onDrag(
                            eventX - dragStartX,
                            eventY - dragStartY,
                            (eventTime - dragStartTime).toInt()
                        )
                    }
                    onTouch(event)
                }
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (!isLongClick) {
                    if (MathUtil.getDistanceXY(eventX, eventY, dragLastX, dragLastY) < 5.0f) {
                        dragStartTime = eventTime
                        dragStartX = eventX
                        dragStartY = eventY
                    }
                    onMove(eventX - dragLastX, eventY - dragLastY, (eventTime - dragLastTime).toInt())
                    dragLastTime = eventTime
                    dragLastX = eventX
                    dragLastY = eventY
                    onTouch(event)
                }
                return true
            }

            else -> {
                onTouch(event)
                return true
            }
        }
    }

    fun getViewWidth(): Int = viewWidth

    fun getViewHeight(): Int = viewHeight

    fun getViewCurrentX(): Float = viewCurrentX

    fun getViewCurrentY(): Float = viewCurrentY

    fun getViewTargetX(): Float = viewTargetX

    fun getViewTargetY(): Float = viewTargetY

    fun setViewTargetX(x: Float) {
        viewTargetX = x
    }

    fun setViewTargetY(y: Float) {
        viewTargetY = y
    }

    fun setViewCurrentX(x: Float) {
        viewCurrentX = x
        viewTargetX = x
    }

    fun setViewCurrentY(y: Float) {
        viewCurrentY = y
        viewTargetY = y
    }

    fun setViewHeight(viewHeight: Int) {
        this.viewHeight = viewHeight
        params?.height = viewHeight
    }

    fun setViewWidth(viewWidth: Int) {
        this.viewWidth = viewWidth
        params?.width = viewWidth
    }

    fun setMoveToTargetBufferedSpeed(moveToTargetBufferedSpeed: Float) {
        this.moveToTargetBufferedSpeed = moveToTargetBufferedSpeed
    }

    fun getParams(): WindowManager.LayoutParams? = params
}