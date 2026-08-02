package k.p.item.specialfood;

import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class Tentacle extends SpecialFood {
    private static final long serialVersionUID = -4595535494962486282L;

    @Override // k.p.item.Item
    public String getName() {
        return "食用幽灵";
    }

    @Override // k.p.item.Item
    public String getItemDescription() {
        return "鱿鱼须而已啦\r\n\r\n+10速度";
    }

    @Override // k.p.item.BaseEatableItem
    protected void onEat(BasePet pet) {
        pet.setSpeed(pet.getSpeed() + 10);
    }
}
