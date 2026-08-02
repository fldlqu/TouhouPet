package k.p.action.workinfo;

import java.util.Random;
import k.p.action.WorkAction;
import k.p.domain.BasePet;
import k.p.item.food.Apple;
import k.p.item.food.Bread;
import k.p.item.food.Cake;
import k.p.item.food.RiceBall;
import k.p.item.food.SteamedBun;
import k.p.services.ItemService;

/* JADX INFO: loaded from: classes.dex */
public class KeepShop extends WorkAction.BaseWorkInfo {
    private static final long serialVersionUID = -6790098864593726211L;

    @Override // k.p.action.WorkAction.WorkInfo
    public void onDone(BasePet pet) {
        pet.changePoint(30);
        int r = new Random().nextInt(100);
        if (r < 20) {
            ItemService.addItem(new RiceBall());
            ItemService.addItem(new RiceBall());
            ItemService.addItem(new RiceBall());
            return;
        }
        if (r < 40) {
            ItemService.addItem(new Bread());
            ItemService.addItem(new Bread());
            ItemService.addItem(new Bread());
        } else if (r < 60) {
            ItemService.addItem(new Cake());
            ItemService.addItem(new Cake());
            ItemService.addItem(new Cake());
        } else if (r < 80) {
            ItemService.addItem(new Apple());
            ItemService.addItem(new Apple());
            ItemService.addItem(new Apple());
        } else {
            ItemService.addItem(new SteamedBun());
            ItemService.addItem(new SteamedBun());
            ItemService.addItem(new SteamedBun());
        }
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getName() {
        return "看店";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public int getMaxDuration() {
        return 7200000;
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getStartMessage() {
        return "消耗时间 : 120分钟\r\n消耗精力 : 1\r\n给乡长看店,包吃住!\r\n需要灵力:50\r\n\r\n奖励 : 点数 x 30";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getDoneMessage() {
        return "点数 + 10\r\n从乡长那里领了今天的便当";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public boolean canDone(BasePet pet) {
        return pet.getMagic() >= 50;
    }
}
