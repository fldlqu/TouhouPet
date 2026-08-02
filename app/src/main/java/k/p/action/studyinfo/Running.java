package k.p.action.studyinfo;

import k.p.action.StudyAction;
import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class Running extends StudyAction.BaseStudyInfo {
    private static final long serialVersionUID = -2190327041412242670L;

    @Override // k.p.action.StudyAction.StudyInfo
    public void onDone(BasePet pet) {
        pet.setSpeed(pet.getSpeed() + 1);
        pet.changeEnergy(-1);
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public String getName() {
        return "跑步";
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public int getMaxDuration() {
        return 3600000;
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public String getStartMessage() {
        return "消耗时间 : 60分钟\r\n消耗精力 : 1\r\n绕地灵殿跑一圈,速度+1";
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public String getDoneMessage() {
        return "跑步完成\r\n速度+1";
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public boolean canDone(BasePet pet) {
        return true;
    }
}
