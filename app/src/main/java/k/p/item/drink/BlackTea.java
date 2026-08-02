package k.p.item.drink;

import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class BlackTea extends Drink {
    private static final long serialVersionUID = -8152667920193288041L;

    @Override // k.p.item.Item
    public String getName() {
        return "红茶";
    }

    @Override // k.p.item.Item
    public String getItemDescription() {
        return "红魔馆偷来的红茶.\r\n\r\n+10饮水度";
    }

    @Override // k.p.item.BaseEatableItem
    protected void onEat(BasePet pet) {
        pet.changeDrinkDegree(10);
    }
}
