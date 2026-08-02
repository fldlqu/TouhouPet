package k.p.item.food;

import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class Meat extends Food {
    private static final long serialVersionUID = 580601877348047194L;

    @Override // k.p.item.Item
    public String getName() {
        return "肉";
    }

    @Override // k.p.item.Item
    public String getItemDescription() {
        return "来路不明的肉...\r\n\r\n+18饱食度";
    }

    @Override // k.p.item.BaseEatableItem
    protected void onEat(BasePet pet) {
        pet.changeRepletionDegree(18);
    }
}
