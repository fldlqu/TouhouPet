package k.p.action.workinfo;

import java.util.Random;
import k.p.action.WorkAction;
import k.p.domain.BasePet;
import k.p.item.drink.BlackTea;
import k.p.item.drink.GreenTea;
import k.p.item.drink.Liquor;
import k.p.item.drink.Rinsing;
import k.p.item.food.Apple;
import k.p.item.food.Bread;
import k.p.item.food.Cake;
import k.p.item.food.Meat;
import k.p.item.food.RiceBall;
import k.p.item.food.SteamedBun;
import k.p.services.DialogService;
import k.p.services.ItemService;

/* JADX INFO: loaded from: classes.dex */
public class Purchase extends WorkAction.BaseWorkInfo {
    private static final long serialVersionUID = 7381250997224681854L;

    @Override // k.p.action.WorkAction.WorkInfo
    public void onDone(BasePet pet) {
        pet.changeEnergy(-1);
        if (pet.getPoint() >= 30) {
            pet.changePoint(-30);
            int r = new Random().nextInt(100);
            if (r < 10) {
                ItemService.addItem(new Bread());
                DialogService.alert("采购完成", "获得 面包 x 1");
                return;
            }
            if (r < 20) {
                ItemService.addItem(new RiceBall());
                DialogService.alert("采购完成", "获得 饭团 x 1");
                return;
            }
            if (r < 30) {
                ItemService.addItem(new Cake());
                DialogService.alert("采购完成", "获得 蛋糕 x 1");
                return;
            }
            if (r < 40) {
                ItemService.addItem(new Meat());
                DialogService.alert("采购完成", "获得 肉 x 1");
                return;
            }
            if (r < 50) {
                ItemService.addItem(new SteamedBun());
                DialogService.alert("采购完成", "获得 包子 x 1");
                return;
            }
            if (r < 60) {
                ItemService.addItem(new Liquor());
                DialogService.alert("采购完成", "获得 酒 x 1");
                return;
            }
            if (r < 70) {
                ItemService.addItem(new Rinsing());
                DialogService.alert("采购完成", "获得 清水 x 1");
            } else if (r < 80) {
                ItemService.addItem(new BlackTea());
                DialogService.alert("采购完成", "获得 红茶 x 1");
            } else if (r < 90) {
                ItemService.addItem(new GreenTea());
                DialogService.alert("采购完成", "获得 清茶 x 1");
            } else {
                ItemService.addItem(new Apple());
                DialogService.alert("采购完成", "获得 苹果 x 1");
            }
        }
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getName() {
        return "采购";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public int getMaxDuration() {
        return 600000;
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getStartMessage() {
        return "消耗时间 : 10分钟\r\n消耗精力 : 1\r\n去人间之里采购吃的\r\n需要点数:30";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public String getDoneMessage() {
        return "";
    }

    @Override // k.p.action.WorkAction.WorkInfo
    public boolean canDone(BasePet pet) {
        return pet.getPoint() >= 30;
    }
}
