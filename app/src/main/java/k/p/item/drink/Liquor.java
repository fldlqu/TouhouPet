package k.p.item.drink;

import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class Liquor extends Drink {
    private static final long serialVersionUID = -2336578421416376745L;

    @Override // k.p.item.Item
    public String getName() {
        return "酒";
    }

    @Override // k.p.item.Item
    public String getItemDescription() {
        return "喝酒有害健康!\r\n\r\n+5饱食度\r\n+10饮水度\r\n-1精力";
    }

    @Override // k.p.item.BaseEatableItem
    protected void onEat(BasePet pet) {
        pet.changeRepletionDegree(5);
        pet.changeDrinkDegree(10);
        pet.changeEnergy(-1);
    }
}
