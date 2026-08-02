package k.p.action.workinfo;

import java.util.Random;
import k.p.action.WorkAction;
import k.p.action.barrage.Reisen;
import k.p.domain.BasePet;
import k.p.domain.Flag;
import k.p.services.DialogService;
import k.p.services.ViewService;
import k.p.view.sliderview.BarrageSliderItemList;

/* JADX INFO: loaded from: classes.dex */
public class TestMedicine extends WorkAction.BaseWorkInfo {
    private static final long serialVersionUID = 3492765116099617968L;

    @Override // k.p.action.WorkAction.WorkInfo
    public void onDone(BasePet pet) {
        int k2 = new Random().nextInt(10);
        pet.changeEnergy(k2 * (-1));
        pet.changeRepletionDegree(k2);
        DialogService.alert("试药", "今天的药好像没什么味道...\r\n\r\n精力 -" + k2 + " , 饱食 +" + k2);
        Integer count = null;
        try {
            count = (Integer) pet.getPetSetting("TestMedicineForReisen");
        } catch (Exception e) {
        }
        if (count == null) {
            count = 0;
        }
        Integer count2 = Integer.valueOf(count.intValue() + 1);
        if (count2.intValue() == 10) {
            pet.setAchievement(pet.getAchievement() + 1);
            pet.setPetSetting("CanBeatReisen", new Flag());
            DialogService.alert("铃仙", "最近感觉工作少了好多呀...可以经常出去玩了!\r\n\r\n新的弹幕对象可选 : 铃仙");
            ViewService.sliderView.addBarrage(new BarrageSliderItemList.BarrageInfo(Reisen.class, "铃仙"));
        }
        pet.setPetSetting("TestMedicineForReisen", count2);
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getName() {
        return "试药";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public int getMaxDuration() {
        return 600000;
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getStartMessage() {
        return "消耗时间 : 10分钟\r\n消耗精力 : ?\r\n替兔子试试师匠的新药?";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getDoneMessage() {
        return "";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public boolean canDone(BasePet pet) {
        return true;
    }
}
