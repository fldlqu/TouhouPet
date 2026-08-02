package k.p.action.workinfo;

import k.p.action.WorkAction;
import k.p.domain.BasePet;
import k.p.item.food.HotSpringEgg;
import k.p.services.ItemService;

/* JADX INFO: loaded from: classes.dex */
public class CarryCorpse extends WorkAction.BaseWorkInfo {
    private static final long serialVersionUID = 7381250997224681854L;

    @Override // k.p.action.WorkAction.WorkInfo
    public void onDone(BasePet pet) {
        ItemService.addItem(new HotSpringEgg());
        ItemService.addItem(new HotSpringEgg());
        ItemService.addItem(new HotSpringEgg());
        ItemService.addItem(new HotSpringEgg());
        ItemService.addItem(new HotSpringEgg());
        pet.changeEnergy(-2);
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getName() {
        return "搬尸体";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public int getMaxDuration() {
        return 3600000;
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getStartMessage() {
        return "消耗时间 : 60分钟\r\n消耗精力 : 2\r\n帮猫车搬尸体\r\n需要力量:30\r\n\r\n奖励 : 温泉蛋 x 5";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getDoneMessage() {
        return "获得温泉蛋 x 5";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public boolean canDone(BasePet pet) {
        return pet.getStrength() >= 30;
    }
}
