package k.p.item.food;

import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class SteamedBun extends Food {
    private static final long serialVersionUID = 2848644284954891552L;

    @Override // k.p.item.Item
    public String getName() {
        return "包子";
    }

    @Override // k.p.item.Item
    public String getItemDescription() {
        return "怎么有这么多物品描述要写!\r\n包子你不认识吗!\r\n\r\n+8饱食度";
    }

    @Override // k.p.item.BaseEatableItem
    protected void onEat(BasePet pet) {
        pet.changeRepletionDegree(8);
    }
}
