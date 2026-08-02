package k.p.item.otheritem;

import k.p.domain.BasePet;
import k.p.domain.Flag;
import k.p.domain.ReturnStatus;
import k.p.services.PetService;

/* JADX INFO: loaded from: classes.dex */
public class Pillow extends OtherItem {
    private static final long serialVersionUID = -135977268234945363L;

    @Override // k.p.item.Item
    public String getName() {
        return "枕头";
    }

    @Override // k.p.item.Item
    public String getItemDescription() {
        return "终于有枕头睡觉了!\r\n\r\n加快睡觉时的精力恢复";
    }

    @Override // k.p.item.Item
    public boolean canUse() {
        return false;
    }

    @Override // k.p.item.Item
    public ReturnStatus<?> use(BasePet pet, Object target) {
        return null;
    }

    @Override // k.p.item.Item
    public void onGet() {
        PetService.pet.setPetSetting("HasPillow", new Flag());
    }

    @Override // k.p.item.Item
    public void onDestroy(int type) {
        PetService.pet.setPetSetting("HasPillow", null);
    }
}
