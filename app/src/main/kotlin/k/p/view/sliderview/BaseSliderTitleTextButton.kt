package k.p.view.sliderview

import android.graphics.Paint
import android.graphics.Rect

open class BaseSliderTitleTextButton(protected var sliderView: SliderView, private var hint: String) : SliderItemView {
    private var position = 0
    private var height = 70
    private var textPaint = Paint().apply {
        color = -16777216
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        textSize = 36.0f
        style = Paint.Style.FILL_AND_STROKE
    }

    override fun getHeight(): Int = (height * sliderView.getYScale()).toInt()

    override fun onDraw(sc: SliderCanvas) {
        if (sliderView.titleTextBGBitmap != null && !sliderView.titleTextBGBitmap!!.isRecycled) {
            sc.drawBitmap(this, sliderView.titleTextBGBitmap!!, null, Rect(1, 10, 159, 60), null)
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
