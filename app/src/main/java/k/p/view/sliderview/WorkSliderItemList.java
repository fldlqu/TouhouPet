package k.p.view.sliderview;

import java.util.ArrayList;
import java.util.List;
import k.p.action.WorkAction;
import k.p.action.workinfo.CarryCorpse;
import k.p.action.workinfo.CleanCourtyardForYoumu;
import k.p.action.workinfo.KeepShop;
import k.p.action.workinfo.PartTimeJob;
import k.p.action.workinfo.Purchase;
import k.p.action.workinfo.PurchaseFromXLT;
import k.p.action.workinfo.SweepFloorForReimu;
import k.p.action.workinfo.TeachMath;
import k.p.action.workinfo.TestMedicine;
import k.p.services.PetService;

/* JADX INFO: loaded from: classes.dex */
public class WorkSliderItemList extends SliderItemList {
    private List<WorkAction.BaseWorkInfo> workInfoList;

    public WorkSliderItemList(SliderView sliderView, SliderCanvas sc) {
        super(sliderView, sc);
    }

    @Override // k.p.view.sliderview.SliderItemList
    public void init() {
        super.init();
        this.workInfoList = new ArrayList();
        this.workInfoList.add(new CarryCorpse());
        this.workInfoList.add(new TeachMath());
        this.workInfoList.add(new PartTimeJob());
        if (PetService.pet.getPetSetting("CanCleanCourtyardForYoumu") != null) {
            this.workInfoList.add(new CleanCourtyardForYoumu());
        }
        this.workInfoList.add(new SweepFloorForReimu());
        this.workInfoList.add(new Purchase());
        this.workInfoList.add(new PurchaseFromXLT());
        this.workInfoList.add(new KeepShop());
        if (PetService.pet.getPetSetting("CanTestMedicine") != null) {
            this.workInfoList.add(new TestMedicine());
        }
        refreshWorkInfo();
    }

    public void refreshWorkInfo() {
        clearSliderItemView();
        for (WorkAction.BaseWorkInfo info : this.workInfoList) {
            addSliderItemView(new WorkButton(info, this.sliderView, info.getName()));
        }
        ReturnButton returnButton = new ReturnButton(this.sliderView) { // from class: k.p.view.sliderview.WorkSliderItemList.1
            @Override // k.p.view.sliderview.ReturnButton, k.p.view.sliderview.BaseSliderButton, k.p.view.sliderview.SliderItemView
            public void onClick() {
                this.sliderView.currentSliderItemList = this.sliderView.actionList;
            }
        };
        returnButton.init();
        addSliderItemView(returnButton);
    }

    public void addWorkInfo(WorkAction.BaseWorkInfo info) {
        if (!this.workInfoList.contains(info)) {
            this.workInfoList.add(info);
            refreshWorkInfo();
        }
    }
}
