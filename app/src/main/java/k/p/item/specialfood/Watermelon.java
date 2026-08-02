package k.p.item.specialfood;

import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class Watermelon extends SpecialFood {
    private static final long serialVersionUID = -4595535494962486282L;

    @Override // k.p.item.Item
    public String getName() {
        return "西瓜";
    }

    @Override // k.p.item.Item
    public String getItemDescription() {
        return "吃西瓜为什么会加力量呢?\r\n\r\n+10力量";
    }

    @Override // k.p.item.BaseEatableItem
    protected void onEat(BasePet pet) {
        pet.setStrength(pet.getStrength() + 10);
    }
}
