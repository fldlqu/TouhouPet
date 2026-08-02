package k.p.item.food;

import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class Cake extends Food {
    private static final long serialVersionUID = 587786511188028578L;

    @Override // k.p.item.Item
    public String getName() {
        return "蛋糕";
    }

    @Override // k.p.item.Item
    public String getItemDescription() {
        return "好吃的蛋糕...\r\n\r\n+15饱食度";
    }

    @Override // k.p.item.BaseEatableItem
    protected void onEat(BasePet pet) {
        pet.changeRepletionDegree(15);
    }
}
