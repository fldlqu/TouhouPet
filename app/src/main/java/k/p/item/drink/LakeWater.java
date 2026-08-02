package k.p.item.drink;

import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class LakeWater extends Drink {
    private static final long serialVersionUID = -6487442980369578755L;

    @Override // k.p.item.Item
    public String getName() {
        return "湖水";
    }

    @Override // k.p.item.Item
    public String getItemDescription() {
        return "这真的能喝吗...\r\n\r\n+3饮水度";
    }

    @Override // k.p.item.BaseEatableItem
    protected void onEat(BasePet pet) {
        pet.changeDrinkDegree(3);
    }
}
