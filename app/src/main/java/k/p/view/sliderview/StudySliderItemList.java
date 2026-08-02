package k.p.view.sliderview;

import java.util.ArrayList;
import java.util.List;
import k.p.action.StudyAction;
import k.p.action.studyinfo.AroundLakeRunning;
import k.p.action.studyinfo.AttendClass;
import k.p.action.studyinfo.BeatBag;
import k.p.action.studyinfo.Exercise;
import k.p.action.studyinfo.Reading;
import k.p.action.studyinfo.Running;
import k.p.services.PetService;

/* JADX INFO: loaded from: classes.dex */
public class StudySliderItemList extends SliderItemList {
    private List<StudyAction.BaseStudyInfo> studyInfoList;

    public StudySliderItemList(SliderView sliderView, SliderCanvas sc) {
        super(sliderView, sc);
    }

    @Override // k.p.view.sliderview.SliderItemList
    public void init() {
        super.init();
        this.studyInfoList = new ArrayList();
        this.studyInfoList.add(new BeatBag());
        this.studyInfoList.add(new Running());
        this.studyInfoList.add(new Reading());
        if (PetService.pet.getPetSetting("CanAttendClass") != null) {
            this.studyInfoList.add(new AttendClass());
        }
        if (PetService.pet.getPetSetting("CanAroundLakeRunning") != null) {
            this.studyInfoList.add(new AroundLakeRunning());
        }
        this.studyInfoList.add(new Exercise());
        refreshStudyInfo();
    }

    public void refreshStudyInfo() {
        clearSliderItemView();
        for (StudyAction.BaseStudyInfo info : this.studyInfoList) {
            addSliderItemView(new StudyButton(info, this.sliderView, info.getName()));
        }
        ReturnButton returnButton = new ReturnButton(this.sliderView) { // from class: k.p.view.sliderview.StudySliderItemList.1
            @Override // k.p.view.sliderview.ReturnButton, k.p.view.sliderview.BaseSliderButton, k.p.view.sliderview.SliderItemView
            public void onClick() {
                this.sliderView.currentSliderItemList = this.sliderView.actionList;
            }
        };
        returnButton.init();
        addSliderItemView(returnButton);
    }

    public void addStudyInfo(StudyAction.BaseStudyInfo info) {
        if (!this.studyInfoList.contains(info)) {
            this.studyInfoList.add(info);
            refreshStudyInfo();
        }
    }
}
