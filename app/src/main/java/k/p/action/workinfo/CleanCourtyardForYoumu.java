package k.p.action.workinfo;

import k.p.action.WorkAction;
import k.p.domain.BasePet;
import k.p.item.drink.Rinsing;
import k.p.services.DialogService;
import k.p.services.ItemService;

/* JADX INFO: loaded from: classes.dex */
public class CleanCourtyardForYoumu extends WorkAction.BaseWorkInfo {
    private static final long serialVersionUID = -5376656616829562509L;

    @Override // k.p.action.WorkAction.WorkInfo
    public void onDone(BasePet pet) {
        ItemService.addItem(new Rinsing());
        ItemService.addItem(new Rinsing());
        ItemService.addItem(new Rinsing());
        pet.changeEnergy(-1);
        Integer count = null;
        try {
            count = (Integer) pet.getPetSetting("CleanCourtyardForYoumu");
        } catch (Exception e) {
        }
        if (count == null) {
            count = 0;
        }
        Integer count2 = Integer.valueOf(count.intValue() + 1);
        if (count2.intValue() == 10) {
            pet.setAchievement(pet.getAchievement() + 1);
            DialogService.alert("妖梦", "总是来帮忙真是不好意思,就教你一些东西吧\r\n\r\n速度+50");
            pet.setSpeed(pet.getSpeed() + 50);
        }
        pet.setPetSetting("CleanCourtyardForYoumu", count2);
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getName() {
        return "清理庭院";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public int getMaxDuration() {
        return 3600000;
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getStartMessage() {
        return "消耗时间 : 60分钟\r\n消耗精力 : 1\r\n帮妖梦给庭院剪剪草,妖梦一定会挨不住面子给我倒一杯水的!\r\n\r\n奖励 : 清水 x 1";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getDoneMessage() {
        return "获得清水 x 3";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public boolean canDone(BasePet pet) {
        return true;
    }
}
