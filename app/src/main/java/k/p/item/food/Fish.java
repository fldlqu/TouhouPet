package k.p.item.food;

import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class Fish extends Food {
    private static final long serialVersionUID = 3919504735744941365L;

    @Override // k.p.item.Item
    public String getName() {
        return "鱼";
    }

    @Override // k.p.item.Item
    public String getItemDescription() {
        return "雾之湖里面的一条鱼.\r\n生吃大丈夫?\r\n\r\n+8饱食度";
    }

    @Override // k.p.item.BaseEatableItem
    protected void onEat(BasePet pet) {
        pet.changeRepletionDegree(8);
    }
}
