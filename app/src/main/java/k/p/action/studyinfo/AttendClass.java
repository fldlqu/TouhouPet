package k.p.action.studyinfo;

import k.p.action.StudyAction;
import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class AttendClass extends StudyAction.BaseStudyInfo {
    private static final long serialVersionUID = 8892668160690373448L;

    @Override // k.p.action.StudyAction.StudyInfo
    public void onDone(BasePet pet) {
        pet.setMagic(pet.getMagic() + 3);
        pet.changeEnergy(-1);
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public String getName() {
        return "上课";
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public int getMaxDuration() {
        return 7200000;
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public String getStartMessage() {
        return "消耗时间 : 120分钟\r\n消耗精力 : 1\r\n去人间之里上慧音的算术课,灵力+3";
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public String getDoneMessage() {
        return "上课完成\r\n灵力+1";
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public boolean canDone(BasePet pet) {
        return true;
    }
}
