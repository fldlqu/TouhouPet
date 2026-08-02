package k.p.view.sliderview

import k.p.action.StudyAction
import k.p.action.studyinfo.AttendClass
import k.p.action.studyinfo.AroundLakeRunning
import k.p.action.studyinfo.BeatBag
import k.p.action.studyinfo.Exercise
import k.p.action.studyinfo.Reading
import k.p.action.studyinfo.Running
import k.p.services.PetService

class StudySliderItemList(sv: SliderView, sc: SliderCanvas) : SliderItemList(sv, sc) {
    private var studyInfoList: MutableList<StudyAction.BaseStudyInfo> = ArrayList()

    override fun init() {
        super.init()
        studyInfoList = ArrayList()
        studyInfoList.add(BeatBag())
        studyInfoList.add(Running())
        studyInfoList.add(Reading())
        if (PetService.pet!!.getPetSetting("CanAttendClass") != null) {
            studyInfoList.add(AttendClass())
        }
        if (PetService.pet!!.getPetSetting("CanAroundLakeRunning") != null) {
            studyInfoList.add(AroundLakeRunning())
        }
        studyInfoList.add(Exercise())
        refreshStudyInfo()
    }

    fun refreshStudyInfo() {
        clearSliderItemView()
        for (info in studyInfoList) {
            addSliderItemView(StudyButton(info, sliderView, info.getName()))
        }
        val returnButton = object : ReturnButton(sliderView) {
            override fun onClick() {
                sliderView.currentSliderItemList = sliderView.actionList
            }
        }
        returnButton.init()
        addSliderItemView(returnButton)
    }

    fun addStudyInfo(info: StudyAction.BaseStudyInfo) {
        if (!studyInfoList.contains(info)) {
            studyInfoList.add(info)
            refreshStudyInfo()
        }
    }
}