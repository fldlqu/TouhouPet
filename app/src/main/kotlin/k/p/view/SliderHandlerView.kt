package k.p.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import k.p.main.R
import k.p.services.ViewService
import k.p.view.sliderview.SliderView

open class SliderHandlerView : BaseDesktopView {
    private var bitmap: Bitmap? = null
    private var drawRect: Rect? = null
    private var drawed = false
    private var sv: SliderView? = null

    constructor(context: Context) : super(context) {
        drawed = false
    }

    override fun init() {
        super.init()
        setCurrentFPS(30.0f)
        setViewHeight((200.0f * Y_SCALE).toInt())
        setViewWidth((100.0f * X_SCALE).toInt())
        setViewCurrentX(0.0f)
        setViewCurrentY(Y_SCALE * 800.0f)
        params!!.y = (Y_SCALE * 800.0f).toInt()
        bitmap = loadBitmap(R.drawable.slider_handler)
        drawRect = Rect(0, 0, viewWidth, viewHeight)
        sv = ViewService.sliderView
    }

    override fun updateStatus(time: Int): Boolean {
        super.updateStatus(time)
        return !drawed
    }

    override fun requestPause() {
        super.requestPause()
        drawed = false
    }

    override fun update(canvas: Canvas) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap!!, null, drawRect!!, picPaint)
            drawed = true
        }
    }

    override fun onMove(x: Float, y: Float, time: Int) {
        var targetX = (getViewCurrentX() + x).toInt()
        if (targetX > sv!!.getViewWidth()) {
            targetX = sv!!.getViewWidth()
        } else if (targetX < 0) {
            targetX = 0
        }
        setViewCurrentX(targetX.toFloat())
        sv!!.setViewCurrentX((targetX - sv!!.getViewWidth()).toFloat())
    }

    override fun onUp(x: Float, y: Float) {
        if (getViewCurrentX() < sv!!.getViewWidth() / 2.0f) {
            setViewTargetX(0.0f)
            sv!!.setViewTargetX(-sv!!.getViewWidth().toFloat())
        } else {
            setViewTargetX(sv!!.getViewWidth().toFloat())
            sv!!.setViewTargetX(0.0f)
        }
    }

    override fun onHide() {
        setViewCurrentX(0.0f)
        params!!.x = 0
    }
}