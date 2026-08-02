package k.p.view.sliderview

interface SliderItemView {
    fun getHeight(): Int
    fun getPosition(): Int
    fun init()
    fun onClick()
    fun onDraw(sliderCanvas: SliderCanvas)
    fun onHide()
    fun onShow()
    fun release()
    fun setPosition(i: Int)
}
