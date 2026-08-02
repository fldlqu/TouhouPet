package k.p.item.food;

import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class HotSpringEgg extends Food {
    private static final long serialVersionUID = -8189683840052818275L;

    @Override // k.p.item.Item
    public String getName() {
        return "温泉蛋";
    }

    @Override // k.p.item.Item
    public String getItemDescription() {
        return "温泉里煮成的蛋...\r\n\r\n+5饱食度";
    }

    @Override // k.p.item.BaseEatableItem
    protected void onEat(BasePet pet) {
        pet.changeRepletionDegree(5);
    }
}
