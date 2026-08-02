package k.p.view.sliderview

import android.graphics.Paint
import android.graphics.Rect

open class BaseSliderTextButton(sv: SliderView, private var hint: String) : SliderItemView {
    @JvmField
    protected var sliderView: SliderView = sv
    private var position = 0
    private var height = 70
    private var textPaint = Paint().apply {
        color = -16777216
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        textSize = 32.0f
        style = Paint.Style.FILL_AND_STROKE
    }

    override fun getHeight(): Int = (height * sliderView.getYScale()).toInt()

    /* 现代化:设定项开关等需要动态更新文字(原版无此方法,纯新增) */
    fun setHint(hint: String) {
        this.hint = hint
    }

    override fun onDraw(sc: SliderCanvas) {
        if (sliderView.textButtonBGBitmap != null && !sliderView.textButtonBGBitmap!!.isRecycled) {
            sc.drawBitmap(this, sliderView.textButtonBGBitmap!!, null, Rect(10, 10, 150, 60), null)
        }
        sc.drawText(this, hint, sliderView.getViewWidth() / 2, 45, textPaint)
    }

    override fun setPosition(position: Int) {
        this.position = position
    }

    override fun getPosition(): Int = position

    override fun init() {
    }

    override fun release() {
    }

    override fun onClick() {
    }

    override fun onShow() {
    }

    override fun onHide() {
    }
}
