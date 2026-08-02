package k.p.action.studyinfo;

import k.p.action.StudyAction;
import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class BeatBag extends StudyAction.BaseStudyInfo {
    private static final long serialVersionUID = -4598500607177856412L;

    @Override // k.p.action.StudyAction.StudyInfo
    public void onDone(BasePet pet) {
        pet.setStrength(pet.getStrength() + 1);
        pet.changeEnergy(-1);
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public String getName() {
        return "打沙包";
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public int getMaxDuration() {
        return 3600000;
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public String getStartMessage() {
        return "消耗时间 : 60分钟\r\n消耗精力 : 1\r\n找个沙包揍一顿,力量+1";
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public String getDoneMessage() {
        return "打沙包完成\r\n力量+1";
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public boolean canDone(BasePet pet) {
        return true;
    }
}
