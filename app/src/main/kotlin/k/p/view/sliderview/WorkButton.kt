package k.p.view.sliderview

import k.p.action.WorkAction
import k.p.services.PetService

class WorkButton(
    private var info: WorkAction.BaseWorkInfo,
    sv: SliderView,
    hint: String
) : BaseSliderTextButton(sv, hint) {

    override fun onClick() {
        WorkAction<WorkAction.BaseWorkInfo>(info).work(PetService.pet)
    }
}