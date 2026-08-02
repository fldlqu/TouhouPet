package k.p.item.specialfood;

import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class PoisonMushroom extends SpecialFood {
    private static final long serialVersionUID = -4595535494962486282L;

    @Override // k.p.item.Item
    public String getName() {
        return "毒蘑菇";
    }

    @Override // k.p.item.Item
    public String getItemDescription() {
        return "良药苦口...\r\n\r\n+10灵力";
    }

    @Override // k.p.item.BaseEatableItem
    protected void onEat(BasePet pet) {
        pet.setMagic(pet.getMagic() + 10);
    }
}
