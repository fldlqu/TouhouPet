package k.p.action.studyinfo;

import java.util.List;
import k.p.action.StudyAction;
import k.p.domain.BasePet;
import k.p.item.BaseItem;
import k.p.services.DialogService;
import k.p.services.ItemService;

/* JADX INFO: loaded from: classes.dex */
public class CarryBrick extends StudyAction.BaseStudyInfo {
    private static final long serialVersionUID = -3521661141177406975L;

    @Override // k.p.action.StudyAction.StudyInfo
    public void onDone(BasePet pet) {
        List<BaseItem> list = ItemService.getItemsByName("酒");
        if (list.size() > 0) {
            ItemService.dropItem(list.get(0));
            pet.setStrength(pet.getStrength() + 20);
        }
        pet.changeEnergy(-3);
        DialogService.alert("红有三", "说好的酒呢!");
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public String getName() {
        return "搬砖";
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public int getMaxDuration() {
        return 3600000;
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public String getStartMessage() {
        return "消耗时间 : 60分钟\r\n消耗精力 : 3\r\n找红有三学习搬砖,效果卓群!\r\n\r\n需要 : 酒 x 1\r\n力量+20";
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public String getDoneMessage() {
        return "搬砖完成\r\n\r\n力量+20";
    }

    @Override // k.p.action.StudyAction.StudyInfo
    public boolean canDone(BasePet pet) {
        return ItemService.getItemsByName("酒").size() > 0;
    }
}
