package k.p.item;

import k.p.domain.BasePet;
import k.p.domain.ReturnStatus;
import k.p.services.ItemService;

/* JADX INFO: loaded from: classes.dex */
public abstract class BaseEatableItem extends BaseItem {
    @Override // k.p.item.Item
    public boolean canUse() {
        return true;
    }

    @Override // k.p.item.Item
    public ReturnStatus<Void> use(BasePet pet, Object target) {
        onEat(pet);
        onDestroy(3);
        ItemService.useItem(this);
        return ReturnStatus.TRUE;
    }

    protected void onEat(BasePet pet) {
    }

    @Override // k.p.item.Item
    public void onGet() {
    }

    @Override // k.p.item.Item
    public void onDestroy(int type) {
    }
}
