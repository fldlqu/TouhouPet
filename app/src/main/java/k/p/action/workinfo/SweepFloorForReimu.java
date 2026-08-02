package k.p.action.workinfo;

import java.util.Random;
import k.p.action.WorkAction;
import k.p.domain.BasePet;
import k.p.item.drink.GreenTea;
import k.p.item.drink.Liquor;
import k.p.services.DialogService;
import k.p.services.ItemService;

/* JADX INFO: loaded from: classes.dex */
public class SweepFloorForReimu extends WorkAction.BaseWorkInfo {
    private static final long serialVersionUID = -643184358151842868L;

    @Override // k.p.action.WorkAction.WorkInfo
    public void onDone(BasePet pet) {
        pet.changeEnergy(-1);
        if (new Random().nextInt(100) < 20) {
            ItemService.addItem(new Liquor());
            ItemService.addItem(new Liquor());
            DialogService.alert("清扫神社完成", "发现2瓶酒!");
        } else {
            ItemService.addItem(new GreenTea());
            ItemService.addItem(new GreenTea());
            ItemService.addItem(new GreenTea());
        }
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getName() {
        return "清扫神社";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public int getMaxDuration() {
        return 3600000;
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getStartMessage() {
        return "消耗时间 : 60分钟\r\n消耗精力 : 1\r\n帮灵梦去扫一下神社,可以蹭到一杯茶喝\r\n\r\n奖励 : 清茶 x 3";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getDoneMessage() {
        return "获得清茶 x 3";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public boolean canDone(BasePet pet) {
        return true;
    }
}
