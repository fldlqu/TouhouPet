package k.p.item.specialfood;

import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class Ghost extends SpecialFood {
    private static final long serialVersionUID = -4595535494962486282L;

    @Override // k.p.item.Item
    public String getName() {
        return "幽灵";
    }

    @Override // k.p.item.Item
    public String getItemDescription() {
        return "集天地之精华,吃了一定功力大涨!\r\n\r\n+5灵力";
    }

    @Override // k.p.item.BaseEatableItem
    protected void onEat(BasePet pet) {
        pet.setMagic(pet.getMagic() + 5);
    }
}
