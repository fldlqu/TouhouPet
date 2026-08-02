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
import k.p.modern.Diag
import k.p.services.AnimationService
import k.p.services.DialogService
import k.p.services.ViewService
import k.p.utils.EnvironmentUtil
import java.util.HashMap

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
        if (DialogService.currentDialogView != null) {
            DialogService.clearDialog()
        }
    }

    override fun onLongClick(x: Float, y: Float) {
        (getContext() as MainService).exit()
    }

    override fun onClick(x: Float, y: Float) {
        if (DialogService.currentDialogView == null && lastClickTime <= 0) {
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