package k.p.action.studyinfo;

import k.p.action.StudyAction;
import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class AroundLakeRunning extends StudyAction.BaseStudyInfo {
    private static final long serialVersionUID = -3521661141177406975L;

    @Override // k.p.action.StudyAction.StudyInfo
    public void onDone(BasePet pet) {
        pet.setSpeed(pet.getSpeed() + 5);
        pet.changeEnergy(-2);
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public String getName() {
        return "绕湖跑步";
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public int getMaxDuration() {
        return 7200000;
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public String getStartMessage() {
        return "消耗时间 : 120分钟\r\n消耗精力 : 2\r\n去雾之湖跑一圈,速度+5";
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public String getDoneMessage() {
        return "绕湖跑步完成\r\n速度+5";
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public boolean canDone(BasePet pet) {
        return true;
    }
}
