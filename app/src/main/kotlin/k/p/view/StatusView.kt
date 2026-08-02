package k.p.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import k.p.domain.BasePet
import k.p.main.MainService
import k.p.main.R

open class StatusView : BaseDesktopView {
    @JvmField
    var stage = 1

    private var bgBitmap: Bitmap? = null
    private var bgPaint: Paint? = null
    private var drawRect: Rect? = null
    private var fadeTime = 0
    private var pet: BasePet
    private var progressBGBitmap: Bitmap? = null
    private var progressRateBitmap: Bitmap? = null
    private var textPaint: Paint? = null
    @JvmField
    protected var viewAlpha = 0f

    constructor(context: Context) : super(context) {
        stage = 1
        pet = (context as MainService).pet!!
    }

    override fun init() {
        super.init()
        setViewWidth((X_SCALE * 300.0f).toInt())
        setViewHeight((Y_SCALE * 500.0f).toInt())
        setCurrentFPS(20.0f)
        setViewCurrentX(X_SCALE * 180.0f)
        params!!.x = (X_SCALE * 180.0f).toInt()
        bgBitmap = loadBitmap(R.drawable.statusview_bg)
        progressBGBitmap = loadBitmap(R.drawable.progress_bg)
        progressRateBitmap = loadBitmap(R.drawable.progress_rate)
        drawRect = Rect(0, 0, (X_SCALE * 300.0f).toInt(), (Y_SCALE * 500.0f).toInt())
        bgPaint = Paint()
        textPaint = Paint()
        textPaint!!.color = -16777216
        textPaint!!.textAlign = Paint.Align.LEFT
        textPaint!!.isAntiAlias = true
        textPaint!!.textSize = 24.0f * X_SCALE
        textPaint!!.style = Paint.Style.FILL_AND_STROKE
        TEXT_X_OFFSET = X_SCALE * 20.0f
        TEXT_Y_OFFSET = 40.0f * Y_SCALE
        TEXT_Y_INTERVAL = 30.0f * Y_SCALE
        PROGRESSBAR_WIDTH = 162.0f * X_SCALE
        PROGRESSBAR_HEIGHT = 10.0f * Y_SCALE
        PROGRESSBAR_X_OFFSET = 70.0f * X_SCALE
        PROGRESSBAR_Y_OFFSET = -13.0f * Y_SCALE
    }

    override fun show() {
        setViewCurrentX(X_SCALE * 180.0f)
        params!!.x = (X_SCALE * 180.0f).toInt()
        setViewCurrentY(0.0f)
        params!!.y = 0
        fadeTime = 1000
        stage = 2
        super.show()
    }

    override fun hide() {
        fadeTime = 1000
        stage = 3
    }

    fun hideImmediately() {
        if (stage != 1) {
            super.hide()
            fadeTime = 0
            stage = 1
            setViewCurrentX(0.0f)
            params!!.x = 0
            setViewAlpha(0.0f)
        }
    }

    override fun updateStatus(time: Int): Boolean {
        fadeTime -= time
        if (fadeTime < 0) {
            fadeTime = 0
        }
        if (stage == 2) {
            if (fadeTime <= 0) {
                stage = 0
            }
            setViewAlpha((1000 - fadeTime) / 1000.0f)
        } else if (stage == 3) {
            if (fadeTime <= 0) {
                stage = 1
                super.hide()
            }
            setViewAlpha(fadeTime / 1000.0f)
        }
        super.updateStatus(time)
        return true
    }

    override fun update(canvas: Canvas) {
        bgPaint!!.alpha = (viewAlpha * 255.0f).toInt()
        textPaint!!.alpha = (viewAlpha * 255.0f).toInt()
        canvas.drawBitmap(bgBitmap!!, null, drawRect!!, clearPaint)
        canvas.drawBitmap(bgBitmap!!, null, drawRect!!, bgPaint)
        textPaint!!.textAlign = Paint.Align.LEFT
        canvas.drawText(pet.getName(), TEXT_X_OFFSET, TEXT_Y_OFFSET + 0.0f * TEXT_Y_INTERVAL, textPaint!!)
        canvas.drawText("等级 : " + pet.getLevel(), TEXT_X_OFFSET, TEXT_Y_OFFSET + 1.0f * TEXT_Y_INTERVAL, textPaint!!)
        canvas.drawText("状态 : " + pet.getCurrentState().getStateDoingDescription(), TEXT_X_OFFSET, TEXT_Y_OFFSET + 2.0f * TEXT_Y_INTERVAL, textPaint!!)
        canvas.drawText("成就: " + pet.getAchievement(), TEXT_X_OFFSET, TEXT_Y_OFFSET + 3.0f * TEXT_Y_INTERVAL, textPaint!!)
        canvas.drawText("成长: " + pet.getLifeTimeByChinese(), TEXT_X_OFFSET, TEXT_Y_OFFSET + 4.0f * TEXT_Y_INTERVAL, textPaint!!)
        canvas.drawText("点数 : " + pet.getPoint(), TEXT_X_OFFSET, TEXT_Y_OFFSET + 10.0f * TEXT_Y_INTERVAL, textPaint!!)
        canvas.drawText("P 点 : " + pet.getPower(), TEXT_X_OFFSET, TEXT_Y_OFFSET + 11.0f * TEXT_Y_INTERVAL, textPaint!!)
        canvas.drawText("力量 : " + pet.getStrength(), TEXT_X_OFFSET, TEXT_Y_OFFSET + 12.0f * TEXT_Y_INTERVAL, textPaint!!)
        canvas.drawText("速度 : " + pet.getSpeed(), TEXT_X_OFFSET, TEXT_Y_OFFSET + 13.0f * TEXT_Y_INTERVAL, textPaint!!)
        canvas.drawText("灵力 : " + pet.getMagic(), TEXT_X_OFFSET, TEXT_Y_OFFSET + 14.0f * TEXT_Y_INTERVAL, textPaint!!)
        drawProgress(canvas, "经验 :", 5, pet.getCurrentExp(), pet.getNextLevelExp())
        drawProgress(canvas, "饱食 :", 6, pet.getRepletionDegree(), 100)
        drawProgress(canvas, "饮水 :", 7, pet.getDrinkDegree(), 100)
        drawProgress(canvas, "精力 :", 8, pet.getEnergy(), 100)
    }

    private fun drawProgress(canvas: Canvas, title: String, index: Int, currentValue: Int, maxValue: Int) {
        textPaint!!.textAlign = Paint.Align.LEFT
        canvas.drawText(title, TEXT_X_OFFSET, TEXT_Y_OFFSET + index * TEXT_Y_INTERVAL, textPaint!!)
        val rate = currentValue / maxValue.toFloat()
        canvas.drawBitmap(
            progressBGBitmap!!, null,
            Rect(
                (TEXT_X_OFFSET + PROGRESSBAR_X_OFFSET).toInt(),
                (TEXT_Y_OFFSET + PROGRESSBAR_Y_OFFSET + index * TEXT_Y_INTERVAL).toInt(),
                (TEXT_X_OFFSET + PROGRESSBAR_X_OFFSET + PROGRESSBAR_WIDTH).toInt(),
                (TEXT_Y_OFFSET + PROGRESSBAR_Y_OFFSET + index * TEXT_Y_INTERVAL + PROGRESSBAR_HEIGHT).toInt()
            ), bgPaint
        )
        canvas.drawBitmap(
            progressRateBitmap!!, null,
            Rect(
                (TEXT_X_OFFSET + PROGRESSBAR_X_OFFSET).toInt(),
                (TEXT_Y_OFFSET + PROGRESSBAR_Y_OFFSET + index * TEXT_Y_INTERVAL).toInt(),
                (TEXT_X_OFFSET + PROGRESSBAR_X_OFFSET + PROGRESSBAR_WIDTH * rate).toInt(),
                (TEXT_Y_OFFSET + PROGRESSBAR_Y_OFFSET + index * TEXT_Y_INTERVAL + PROGRESSBAR_HEIGHT).toInt()
            ), bgPaint
        )
        textPaint!!.textAlign = Paint.Align.CENTER
        canvas.drawText("$currentValue/$maxValue", TEXT_X_OFFSET + PROGRESSBAR_X_OFFSET + PROGRESSBAR_WIDTH / 2.0f, TEXT_Y_OFFSET + index * TEXT_Y_INTERVAL, textPaint!!)
    }

    fun getViewAlpha(): Float = viewAlpha

    fun setViewAlpha(viewAlpha0: Float) {
        viewAlpha = viewAlpha0
        if (viewAlpha > 1.0f) {
            viewAlpha = 1.0f
        } else if (viewAlpha < 0.0f) {
            viewAlpha = 0.0f
        }
    }

    override fun onMove(x: Float, y: Float, time: Int) {
        setViewCurrentX(getViewCurrentX() + x)
        setViewCurrentY(getViewCurrentY() + y)
    }

    companion object {
        private var PROGRESSBAR_HEIGHT = 0f
        private var PROGRESSBAR_WIDTH = 0f
        private var PROGRESSBAR_X_OFFSET = 0f
        private var PROGRESSBAR_Y_OFFSET = 0f
        private var TEXT_X_OFFSET = 0f
        private var TEXT_Y_INTERVAL = 0f
        private var TEXT_Y_OFFSET = 0f
    }
}