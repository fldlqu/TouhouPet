package k.p.action.studyinfo;

import k.p.action.StudyAction;
import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class Reading extends StudyAction.BaseStudyInfo {
    private static final long serialVersionUID = 1226240232490062434L;

    @Override // k.p.action.StudyAction.StudyInfo
    public void onDone(BasePet pet) {
        pet.setMagic(pet.getMagic() + 1);
        pet.changeEnergy(-1);
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public String getName() {
        return "读书";
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public int getMaxDuration() {
        return 3600000;
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public String getStartMessage() {
        return "消耗时间 : 60分钟\r\n消耗精力 : 1\r\n随便找本魔法书自学,灵力+1";
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public String getDoneMessage() {
        return "读书完成\r\n灵力+1";
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public boolean canDone(BasePet pet) {
        return true;
    }
}
