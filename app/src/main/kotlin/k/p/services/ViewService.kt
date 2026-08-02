package k.p.services

import k.p.view.PetView
import k.p.view.SliderHandlerView
import k.p.view.StatusView
import k.p.view.sliderview.SliderView

object ViewService {
    @JvmField
    var petView: PetView? = null
    @JvmField
    var sliderHandlerView: SliderHandlerView? = null
    @JvmField
    var sliderView: SliderView? = null
    @JvmField
    var statusView: StatusView? = null

    @JvmStatic
    fun release() {
        petView = null
        sliderView = null
        sliderHandlerView = null
        statusView = null
    }
}