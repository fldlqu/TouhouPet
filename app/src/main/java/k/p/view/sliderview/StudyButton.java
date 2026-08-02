package k.p.view.sliderview;

import k.p.action.StudyAction;
import k.p.services.PetService;

/* JADX INFO: loaded from: classes.dex */
public class StudyButton extends BaseSliderTextButton {
    private StudyAction.BaseStudyInfo info;

    public StudyButton(StudyAction.BaseStudyInfo info, SliderView sliderView, String hint) {
        super(sliderView, hint);
        this.info = info;
    }

    @Override // k.p.view.sliderview.BaseSliderTextButton, k.p.view.sliderview.SliderItemView
    public void onClick() {
        new StudyAction(this.info).study(PetService.pet);
    }
}
