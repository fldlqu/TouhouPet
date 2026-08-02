package k.p.item.food;

import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class RiceBall extends Food {
    private static final long serialVersionUID = 2761421121092842470L;

    @Override // k.p.item.Item
    public String getName() {
        return "饭团";
    }

    @Override // k.p.item.Item
    public String getItemDescription() {
        return "...\r\n\r\n+5饱食度";
    }

    @Override // k.p.item.BaseEatableItem
    protected void onEat(BasePet pet) {
        pet.changeRepletionDegree(5);
    }
}
