package k.p.item.food;

import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class Bread extends Food {
    private static final long serialVersionUID = 7424673612741341974L;

    @Override // k.p.item.Item
    public String getName() {
        return "面包";
    }

    @Override // k.p.item.Item
    public String getItemDescription() {
        return "怎么有这么多物品描述要写!\r\n面包你不认识吗!\r\n\r\n+10饱食度";
    }

    @Override // k.p.item.BaseEatableItem
    protected void onEat(BasePet pet) {
        pet.changeRepletionDegree(10);
    }
}
