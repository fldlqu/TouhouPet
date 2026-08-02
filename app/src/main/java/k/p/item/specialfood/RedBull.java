package k.p.item.specialfood;

import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class RedBull extends SpecialFood {
    private static final long serialVersionUID = -4595535494962486282L;

    @Override // k.p.item.Item
    public String getName() {
        return "红牛";
    }

    @Override // k.p.item.Item
    public String getItemDescription() {
        return "外界来的饮料\r\n\r\n+10精力";
    }

    @Override // k.p.item.BaseEatableItem
    protected void onEat(BasePet pet) {
        pet.changeEnergy(10);
    }
}
