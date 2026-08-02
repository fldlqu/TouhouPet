package k.p.item.drink;

import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class Rinsing extends Drink {
    private static final long serialVersionUID = -1216715133079942049L;

    @Override // k.p.item.Item
    public String getName() {
        return "清水";
    }

    @Override // k.p.item.Item
    public String getItemDescription() {
        return "普通的水\r\n\r\n+5饮水度";
    }

    @Override // k.p.item.BaseEatableItem
    protected void onEat(BasePet pet) {
        pet.changeDrinkDegree(5);
    }
}
