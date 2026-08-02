package k.p.action.workinfo;

import k.p.action.WorkAction;
import k.p.domain.BasePet;
import k.p.item.food.Mushroom;
import k.p.services.ItemService;

/* JADX INFO: loaded from: classes.dex */
public class PartnerTraining extends WorkAction.BaseWorkInfo {
    private static final long serialVersionUID = 5873526598315628007L;

    @Override // k.p.action.WorkAction.WorkInfo
    public void onDone(BasePet pet) {
        ItemService.addItem(new Mushroom());
        ItemService.addItem(new Mushroom());
        ItemService.addItem(new Mushroom());
        ItemService.addItem(new Mushroom());
        ItemService.addItem(new Mushroom());
        pet.changeEnergy(-1);
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getName() {
        return "陪练";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public int getMaxDuration() {
        return 1800000;
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getStartMessage() {
        return "消耗时间 : 30分钟\r\n消耗精力 : 1\r\n和魔理沙玩玩弹幕\r\n需要灵力:30\r\n\r\n奖励 : 蘑菇 x 5";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getDoneMessage() {
        return "获得蘑菇 x 5";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public boolean canDone(BasePet pet) {
        return pet.getStrength() >= 30;
    }
}
