package k.p.view.sliderview

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect

interface SliderCanvas {
    fun drawBitmap(sliderItemView: SliderItemView, bitmap: Bitmap, rect: Rect?, rect2: Rect?, paint: Paint?)
    fun drawText(sliderItemView: SliderItemView, str: String, i: Int, i2: Int, paint: Paint?)
    fun setCanvas(canvas: Canvas?)
}
