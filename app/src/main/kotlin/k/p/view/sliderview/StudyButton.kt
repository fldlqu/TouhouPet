package k.p.view.sliderview

import k.p.action.StudyAction
import k.p.services.PetService

class StudyButton(
    private var info: StudyAction.BaseStudyInfo,
    sv: SliderView,
    hint: String
) : BaseSliderTextButton(sv, hint) {

    override fun onClick() {
        StudyAction<StudyAction.BaseStudyInfo>(info).study(PetService.pet)
    }
}