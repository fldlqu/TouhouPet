package k.p.action.workinfo;

import java.util.Random;
import k.p.action.WorkAction;
import k.p.domain.BasePet;
import k.p.item.food.Apple;
import k.p.item.specialfood.PoisonMushroom;
import k.p.item.specialfood.RedBull;
import k.p.item.specialfood.Tentacle;
import k.p.item.specialfood.Watermelon;
import k.p.services.DialogService;
import k.p.services.ItemService;

/* JADX INFO: loaded from: classes.dex */
public class PurchaseFromXLT extends WorkAction.BaseWorkInfo {
    private static final long serialVersionUID = 7381250997224681854L;

    @Override // k.p.action.WorkAction.WorkInfo
    public void onDone(BasePet pet) {
        pet.changeEnergy(-1);
        if (pet.getPoint() >= 100) {
            pet.changePoint(-100);
            int r = new Random().nextInt(100);
            if (r < 10) {
                ItemService.addItem(new PoisonMushroom());
                DialogService.alert("淘宝完成", "获得 毒蘑菇 x 1");
                return;
            }
            if (r < 20) {
                ItemService.addItem(new Tentacle());
                DialogService.alert("淘宝完成", "获得 触手 x 1");
            } else if (r < 30) {
                ItemService.addItem(new Watermelon());
                DialogService.alert("淘宝完成", "获得 西瓜 x 1");
            } else if (r < 40) {
                ItemService.addItem(new RedBull());
                DialogService.alert("淘宝完成", "获得 红牛 x 1");
            } else {
                ItemService.addItem(new Apple());
                DialogService.alert("淘宝完成", "获得 苹果 x 1");
            }
        }
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getName() {
        return "淘宝";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public int getMaxDuration() {
        return 600000;
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getStartMessage() {
        return "消耗时间 : 10分钟\r\n消耗精力 : 1\r\n去香霖堂看看有什么好东西买\r\n需要点数:100";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getDoneMessage() {
        return "";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public boolean canDone(BasePet pet) {
        return pet.getPoint() >= 100;
    }
}
