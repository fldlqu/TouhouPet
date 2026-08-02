package k.p.view.sliderview

import k.p.action.WorkAction
import k.p.action.workinfo.CarryCorpse
import k.p.action.workinfo.CleanCourtyardForYoumu
import k.p.action.workinfo.KeepShop
import k.p.action.workinfo.PartTimeJob
import k.p.action.workinfo.Purchase
import k.p.action.workinfo.PurchaseFromXLT
import k.p.action.workinfo.SweepFloorForReimu
import k.p.action.workinfo.TeachMath
import k.p.action.workinfo.TestMedicine
import k.p.services.PetService

class WorkSliderItemList(sliderView: SliderView, sc: SliderCanvas) : SliderItemList(sliderView, sc) {
    private var workInfoList: MutableList<WorkAction.BaseWorkInfo> = ArrayList()

    override fun init() {
        super.init()
        workInfoList = ArrayList()
        workInfoList.add(CarryCorpse())
        workInfoList.add(TeachMath())
        workInfoList.add(PartTimeJob())
        if (PetService.pet!!.getPetSetting("CanCleanCourtyardForYoumu") != null) {
            workInfoList.add(CleanCourtyardForYoumu())
        }
        workInfoList.add(SweepFloorForReimu())
        workInfoList.add(Purchase())
        workInfoList.add(PurchaseFromXLT())
        workInfoList.add(KeepShop())
        if (PetService.pet!!.getPetSetting("CanTestMedicine") != null) {
            workInfoList.add(TestMedicine())
        }
        refreshWorkInfo()
    }

    fun refreshWorkInfo() {
        clearSliderItemView()
        for (info in workInfoList) {
            addSliderItemView(WorkButton(info, sliderView, info.getName()))
        }
        val returnButton = object : ReturnButton(sliderView) {
            override fun onClick() {
                sliderView.currentSliderItemList = sliderView.actionList
            }
        }
        returnButton.init()
        addSliderItemView(returnButton)
    }

    fun addWorkInfo(info: WorkAction.BaseWorkInfo) {
        if (!workInfoList.contains(info)) {
            workInfoList.add(info)
            refreshWorkInfo()
        }
    }
}