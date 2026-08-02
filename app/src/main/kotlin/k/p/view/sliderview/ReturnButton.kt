package k.p.view.sliderview

import k.p.main.R

open class ReturnButton(sv: SliderView) : BaseSliderButton(sv, "返回", R.drawable.button_return) {

    override fun onClick() {
        sliderView.currentSliderItemList = sliderView.mainList
    }
}