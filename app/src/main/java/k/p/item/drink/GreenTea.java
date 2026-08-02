package k.p.item.drink;

import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class GreenTea extends Drink {
    private static final long serialVersionUID = -7982192830310975224L;

    @Override // k.p.item.Item
    public String getName() {
        return "清茶";
    }

    @Override // k.p.item.Item
    public String getItemDescription() {
        return "来之不易啊...\r\n\r\n+10饮水度";
    }

    @Override // k.p.item.BaseEatableItem
    protected void onEat(BasePet pet) {
        pet.changeDrinkDegree(10);
    }
}
