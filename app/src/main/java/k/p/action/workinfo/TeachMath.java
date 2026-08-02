package k.p.action.workinfo;

import k.p.action.WorkAction;
import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class TeachMath extends WorkAction.BaseWorkInfo {
    private static final long serialVersionUID = 4196701043096887916L;

    @Override // k.p.action.WorkAction.WorkInfo
    public void onDone(BasePet pet) {
        pet.changePower(30);
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getName() {
        return "教算术";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public int getMaxDuration() {
        return 3600000;
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getStartMessage() {
        return "消耗时间 : 60分钟\r\n消耗精力 : 1\r\n教阿空学算术\r\n\r\n奖励 : P点 X 30";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getDoneMessage() {
        return "阿空又学会了一个一位数加法!\r\nP点 + 30";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public boolean canDone(BasePet pet) {
        return true;
    }
}
