package k.p.view.sliderview;

import k.p.action.WorkAction;
import k.p.services.PetService;

/* JADX INFO: loaded from: classes.dex */
public class WorkButton extends BaseSliderTextButton {
    private WorkAction.BaseWorkInfo info;

    public WorkButton(WorkAction.BaseWorkInfo info, SliderView sliderView, String hint) {
        super(sliderView, hint);
        this.info = info;
    }

    @Override // k.p.view.sliderview.BaseSliderTextButton, k.p.view.sliderview.SliderItemView
    public void onClick() {
        new WorkAction(this.info).work(PetService.pet);
    }
}
