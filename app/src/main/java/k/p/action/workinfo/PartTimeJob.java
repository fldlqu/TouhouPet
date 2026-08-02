package k.p.action.workinfo;

import k.p.action.WorkAction;
import k.p.domain.BasePet;
import k.p.item.food.RiceBall;
import k.p.services.ItemService;

/* JADX INFO: loaded from: classes.dex */
public class PartTimeJob extends WorkAction.BaseWorkInfo {
    private static final long serialVersionUID = -6807127139974465723L;

    @Override // k.p.action.WorkAction.WorkInfo
    public void onDone(BasePet pet) {
        pet.changePoint(10);
        ItemService.addItem(new RiceBall());
        ItemService.addItem(new RiceBall());
        ItemService.addItem(new RiceBall());
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getName() {
        return "打杂";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public int getMaxDuration() {
        return 1800000;
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getStartMessage() {
        return "消耗时间 : 30分钟\r\n消耗精力 : 1\r\n去人间之里打零工\r\n\r\n奖励 : 点数 x 10 饭团 x 3";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getDoneMessage() {
        return "点数 + 10 \r\n获得 饭团 x 3";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public boolean canDone(BasePet pet) {
        return true;
    }
}
