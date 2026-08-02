package k.p.item.food;

import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class Apple extends Food {
    private static final long serialVersionUID = 580601877348047194L;

    @Override // k.p.item.Item
    public String getName() {
        return "苹果";
    }

    @Override // k.p.item.Item
    public String getItemDescription() {
        return "随处可见\r\n\r\n+3饱食度";
    }

    @Override // k.p.item.BaseEatableItem
    protected void onEat(BasePet pet) {
        pet.changeRepletionDegree(3);
    }
}
