package k.p.view.sliderview

import k.p.domain.BasePet
import k.p.domain.states.ActiveState
import k.p.location.Location
import k.p.main.R
import k.p.services.DialogService
import k.p.services.LocationService
import k.p.services.StateService
import k.p.services.ViewService

class LocationSliderItemList(location: Location, sv: SliderView, sc: SliderCanvas) :
    SliderItemList(sv, sc) {
    protected var location: Location = location

    override fun init() {
        super.init()
        refreshLocation()
    }

    fun refreshLocation() {
        clearSliderItemView()
        val button = BaseSliderTitleTextButton(sliderView, location.getName())
        addSliderItemView(button)
        for (l in location.getNearbyList()) {
            if (l.isVisible()) {
                val button2 = object : BaseSliderTextButton(sliderView, l.getName()) {
                    override fun onClick() {
                        val ms = sliderView.mainService!!
                        val mp = ms.pet!!
                        if (mp.currentState is ActiveState) {
                            sliderView.currentSliderItemList = sliderView.locationMap!![l]
                            l.onEnter()
                        } else {
                            val str = "当前正在" + mp.currentState.getStateDoingDescription() +
                                "\r\n确定要移动至" + l.getName() + "吗?"
                            val location = l
                            DialogService.confirm("移动", str, object : DialogService.CallBack {
                                override fun onReturn(retVal: Boolean) {
                                    if (retVal) {
                                        sliderView.mainService!!.pet!!!!.requestChangeState(StateService.ACTIVE, BasePet.MAX_LEVEL)
                                        sliderView.currentSliderItemList = sliderView.locationMap!![location]
                                        location.onEnter()
                                    }
                                }
                            })
                        }
                    }
                }
                addSliderItemView(button2)
            }
        }
        val button3 = object : BaseSliderButton(sliderView, "属性", R.drawable.button_status) {
            override fun onClick() {
                when (ViewService.statusView!!.stage) {
                    0 -> ViewService.statusView!!.hide()
                    1 -> ViewService.statusView!!.show()
                }
            }
        }
        addSliderItemView(button3)
        addSliderItemView(object : BaseSliderButton(sliderView, "回家", R.drawable.button_home) {
            override fun onClick() {
                if (sliderView.mainService!!.pet!!!!.currentState is ActiveState) {
                    sliderView.currentSliderItemList = sliderView.mainList
                    sliderView.mainService!!.pet!!!!.setCurrentLocation(LocationService.HOME)
                    return
                }
                DialogService.confirm("回家",
                    "当前正在" + sliderView.mainService!!.pet!!!!.currentState.getStateDoingDescription() + "\r\n确定要回家吗?",
                    object : DialogService.CallBack {
                        override fun onReturn(retVal: Boolean) {
                            if (retVal) {
                                sliderView.mainService!!.pet!!!!.requestChangeState(StateService.ACTIVE, BasePet.MAX_LEVEL)
                                sliderView.currentSliderItemList = sliderView.mainList
                                sliderView.mainService!!.pet!!!!.setCurrentLocation(LocationService.HOME)
                            }
                        }
                    })
            }
        })
    }
}