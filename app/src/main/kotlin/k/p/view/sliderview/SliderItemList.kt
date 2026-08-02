package k.p.view.sliderview

import java.util.Vector

open class SliderItemList(sv: SliderView, sc: SliderCanvas) {
    @JvmField
    var currentPosition = 0f
    @JvmField
    var targetPosition = 0f
    @JvmField
    var itemListHeight = 0
    @JvmField
    var sc: SliderCanvas = sc
    @JvmField
    var sliderItemList: MutableList<SliderItemView> = Vector()
    @JvmField
    var sliderView: SliderView = sv

    init {
        sliderView.allList.add(this)
    }

    open fun onHide() {
        for (view in sliderItemList) {
            view.onHide()
        }
    }

    open fun onShow() {
        for (view in sliderItemList) {
            view.onShow()
        }
    }

    open fun init() {
        sliderItemList = Vector()
    }

    open fun release() {
        for (view in sliderItemList) {
            view.release()
        }
    }

    open fun addSliderItemView(view: SliderItemView) {
        view.setPosition(itemListHeight)
        sliderItemList.add(view)
        itemListHeight += view.getHeight()
    }

    open fun clearSliderItemView() {
        sliderItemList.clear()
        itemListHeight = 0
    }
}
