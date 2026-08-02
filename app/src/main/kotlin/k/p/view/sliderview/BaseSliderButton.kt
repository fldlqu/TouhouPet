package k.p.view.sliderview

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Rect
import local.kcn.utils.LogUtil

open class BaseSliderButton(
    sv: SliderView,
    private var hint: String,
    private var bitmapId: Int
) : SliderItemView {
    @JvmField
    protected var sliderView: SliderView = sv
    private var bitmap: Bitmap? = null
    private var height = 180
    private var position = 0
    private var textPaint: Paint? = null

    override fun getHeight(): Int = (height * sliderView.getYScale()).toInt()

    override fun onDraw(sc: SliderCanvas) {
        try {
            if (sliderView.textBGBitmap != null && !sliderView.textBGBitmap!!.isRecycled) {
                sc.drawBitmap(this, sliderView.textBGBitmap!!, null, Rect(10, 150, 150, 180), null)
            }
            if (bitmap != null && !bitmap!!.isRecycled) {
                sc.drawBitmap(this, bitmap!!, null, Rect(10, 10, 150, 150), null)
            }
        } catch (e: Exception) {
            LogUtil.log(e)
        }
        sc.drawText(this, hint, sliderView.getViewWidth() / 2, 172, textPaint)
    }

    fun setHint(hint: String) {
        this.hint = hint
    }

    fun setBitmapId(bitmapId: Int) {
        this.bitmapId = bitmapId
    }

    override fun setPosition(position: Int) {
        this.position = position
    }

    override fun getPosition(): Int = position

    override fun init() {
        textPaint = Paint().apply {
            color = -16777216
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            textSize = 24.0f
            style = Paint.Style.FILL_AND_STROKE
        }
        initBitmap()
    }

    private fun initBitmap() {
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(sliderView.resources, bitmapId)
        }
    }

    private fun asyncInitBitmap() {
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(sliderView.resources, bitmapId)
        }
    }

    private fun releaseBitmap() {
        if (bitmap != null) {
            if (!bitmap!!.isRecycled) {
                bitmap!!.recycle()
            }
            bitmap = null
        }
    }

    override fun onShow() {
        asyncInitBitmap()
    }

    override fun onHide() {
        releaseBitmap()
    }

    override fun release() {
    }

    override fun onClick() {
    }
}
