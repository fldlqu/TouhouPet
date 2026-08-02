package k.p.item.food;

import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class BambooShoot extends Food {
    private static final long serialVersionUID = -4085174699844565149L;

    @Override // k.p.item.Item
    public String getName() {
        return "竹笋";
    }

    @Override // k.p.item.Item
    public String getItemDescription() {
        return "别名笋,为多年生常绿草本植物,食用部分为初生、嫩肥、短壮的芽或鞭.\r\n\r\n+8饱食度";
    }

    @Override // k.p.item.BaseEatableItem
    protected void onEat(BasePet pet) {
        pet.changeRepletionDegree(8);
    }
}
