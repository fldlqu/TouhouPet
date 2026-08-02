package k.p.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import k.p.animation.PetAnimation
import k.p.animation.PetAnimationInfo
import k.p.domain.BasePet
import k.p.domain.states.SleepState
import k.p.main.MainService
import k.p.modern.AppScopes
import k.p.modern.Diag
import k.p.services.AnimationService
import k.p.services.DialogService
import k.p.services.ViewService
import k.p.utils.EnvironmentUtil
import java.util.HashMap
import kotlin.math.abs
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

open class PetView : BaseDesktopView {
    private val bitmapMap: MutableMap<String, Bitmap> = HashMap()
    private var currentAnimation: PetAnimation? = null
    private var currentAnimationInfo: PetAnimationInfo? = null
    private var currentBitmap: Bitmap? = null
    private var lastBitmap: Bitmap? = null
    private var lastClickTime = 0
    private var pet: BasePet
    private var rect: Rect? = null
    private var showControl = false
    private var animMissingLogged = false
    private var totalTime = 0

    /** 摇晃撞边动画 scope: 段1 走到边缘 → 段2 弹回; 新摇晃取消旧动画 */
    private val moveScope = AppScopes.newMain()
    private var moveJob: Job? = null

    constructor(context: Context) : super(context) {
        this.pet = (context as MainService).pet!!
        lastBitmap = null
        currentBitmap = null
        showControl = false
    }

    override fun init() {
        super.init()
        if (AnimationService.petHeight > 0 && AnimationService.petWidth > 0) {
            setViewWidth(AnimationService.petWidth)
            setViewHeight(AnimationService.petHeight)
        } else {
            setViewWidth(128)
            setViewHeight(128)
        }
        setViewCurrentX((SCREEN_WIDTH / 2).toFloat())
        setViewCurrentY((SCREEN_HEIGHT / 2).toFloat())
        params!!.x = SCREEN_WIDTH / 2
        params!!.y = SCREEN_HEIGHT / 2
        setCurrentFPS(40.0f)
        rect = Rect(0, 0, viewWidth, viewHeight)
    }

    override fun release() {
        moveJob?.cancel()
        super.release()
    }

    override fun asyncInit() {
        try {
            Thread.sleep(50L)
        } catch (e: InterruptedException) {
        }
        Diag.log("asyncInit: request animation")
        try {
            newRandomAnimation()
        } catch (t: Throwable) {
            Diag.log("asyncInit EX: " + t.javaClass.simpleName + ": " + t.message)
            for (st in t.stackTrace) {
                Diag.log("    at " + st)
            }
        }
    }

    private fun getBitmap(picPath: String): Bitmap? {
        val bitmap = bitmapMap[picPath]
        if (bitmap == null) {
            val bitmap2 = loadBitmap(EnvironmentUtil.getMainPath() + "/pet/animations" + picPath)
            if (bitmap2 != null) {
                bitmapMap[picPath] = bitmap2
            } else {
                Diag.log("loadBitmap FAIL: " + picPath)
            }
            return bitmap2
        }
        return bitmap
    }

    override fun updateStatus(time: Int): Boolean {
        super.updateStatus(time)
        lastClickTime -= time
        totalTime += time
        if (currentAnimationInfo == null) {
            if (!animMissingLogged) {
                animMissingLogged = true
                Diag.log("updateStatus: currentAnimationInfo null")
            }
            /* 动画数据缺失(数据目录无 pet/animations)时保持空白,不崩溃;
             * 原版用户数据齐全不会走到这里 */
            return false
        }
        if (totalTime > currentAnimationInfo!!.delay) {
            currentAnimationInfo = currentAnimation!!.nextFrame()
            if (currentAnimationInfo == null) {
                newRandomAnimation()
            }
            totalTime = 0
        }
        lastBitmap = currentBitmap
        currentBitmap = getBitmap(currentAnimationInfo!!.picPath!!)
        return lastBitmap !== currentBitmap
    }

    private fun newRandomAnimation() {
        val p = pet
        Diag.log("newRandomAnimation: pet=" + (p?.let { it.javaClass.simpleName } ?: "NULL"))
        val state = try {
            p?.currentState?.javaClass?.simpleName ?: "null"
        } catch (t: Throwable) {
            "EX: " + t.javaClass.simpleName
        }
        Diag.log("newRandomAnimation: currentState=" + state)
        if (p != null && p.currentState is SleepState) {
            AnimationService.requestChangeAnimation(AnimationService.getRandomAnimationByType("SLEEP"))
        } else {
            AnimationService.requestChangeAnimation(AnimationService.getRandomAnimationByType("ACTIVE"))
        }
    }

    fun changeAnimation(animation: PetAnimation) {
        currentAnimation = animation
        currentAnimationInfo = animation.nextFrame()
    }

    /* 摇晃移动:沿摇晃方向施加一步力(步长为屏宽/4)。
     * 力足够到达屏幕边缘时, 分两段动画: 段1 先走到边缘, 段2 从边缘弹回剩余力。
     * 例: 力 2、到边缘距离 1 → 先走 1 到边缘, 再弹回 1。
     * 不越界则直接一步到位。 */
    fun moveByDirection(ax: Float, ay: Float) {
        val len = kotlin.math.sqrt(ax * ax + ay * ay)
        if (len < 1e-3f) {
            return
        }
        val step = (SCREEN_WIDTH / 4).coerceAtLeast(200).toFloat()
        /* x 同号(都向右为正);y 取反:加速度计 y 向上为正, 屏幕 y 向下为正 */
        val ux = ax / len
        val uy = -ay / len
        val maxX = (SCREEN_WIDTH - viewWidth).coerceAtLeast(0)
        val maxY = (SCREEN_HEIGHT - viewHeight).coerceAtLeast(0)
        val tx = getViewCurrentX() + ux * step
        val ty = getViewCurrentY() + uy * step
        /* 未越界: 直接移动到目标 */
        if (tx in 0f..maxX.toFloat() && ty in 0f..maxY.toFloat()) {
            setViewTargetX(tx)
            setViewTargetY(ty)
            return
        }
        /* 越界: 两段式撞边动画(新摇晃打断旧动画) */
        moveJob?.cancel()
        moveJob = moveScope.launch {
            /* 段1: 走到边缘(目标夹到边界)*/
            val edgeX = tx.coerceIn(0f, maxX.toFloat())
            val edgeY = ty.coerceIn(0f, maxY.toFloat())
            setViewTargetX(edgeX)
            setViewTargetY(edgeY)
            /* 等待到达边缘(updateStatus 逐帧趋近, 轮询检测; 超时兜底)*/
            val waitUntil = System.currentTimeMillis() + 2000
            while (System.currentTimeMillis() < waitUntil) {
                val dx = abs(getViewCurrentX() - edgeX)
                val dy = abs(getViewCurrentY() - edgeY)
                if (dx <= 2f && dy <= 2f) {
                    break
                }
                delay(16)
            }
            /* 段2: 从边缘弹回剩余力(镜像反射)*/
            var bx = tx
            var by = ty
            if (bx > maxX) {
                bx = (2 * maxX - bx).coerceAtLeast(0f)
            } else if (bx < 0f) {
                bx = -bx
            }
            if (by > maxY) {
                by = (2 * maxY - by).coerceAtLeast(0f)
            } else if (by < 0f) {
                by = -by
            }
            setViewTargetX(bx)
            setViewTargetY(by)
        }
    }

    override fun onMove(x: Float, y: Float, time: Int) {
        setViewCurrentX((getViewCurrentX() + x).toInt().toFloat())
        setViewCurrentY((getViewCurrentY() + y).toInt().toFloat())
    }

    override fun update(canvas: Canvas) {
        canvas.drawPaint(clearPaint!!)
        if (currentBitmap != null) {
            canvas.drawBitmap(currentBitmap!!, null, rect!!, picPaint)
        }
    }

    override fun onHide() {
        if (showControl) {
            ViewService.sliderHandlerView!!.hide()
            ViewService.sliderView!!.hide()
            showControl = false
        }
        if (DialogService.currentDialog != null) {
            DialogService.clearDialog()
        }
    }

    override fun onLongClick(x: Float, y: Float) {
        (getContext() as MainService).exit()
    }

    override fun onClick(x: Float, y: Float) {
        if (DialogService.currentDialog == null && lastClickTime <= 0) {
            lastClickTime = MIN_CLICK_INTERVAL
            if (showControl) {
                ViewService.sliderHandlerView!!.hide()
                ViewService.sliderView!!.hide()
                showControl = false
            } else {
                ViewService.sliderHandlerView!!.show()
                ViewService.sliderView!!.show()
                showControl = true
            }
        }
    }

    companion object {
        private const val MIN_CLICK_INTERVAL = 500
    }
}