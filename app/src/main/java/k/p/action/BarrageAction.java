package k.p.action;

import k.p.action.barrage.BaseEnemy;
import k.p.domain.BasePet;
import k.p.services.BarrageService;
import k.p.services.DialogService;

/* JADX INFO: loaded from: classes.dex */
public class BarrageAction<T extends BaseEnemy> extends BaseImmediatelyAction<T> {
    private static final String actionDescription = "弹幕";
    private static final String actionTag = "barrage";
    private static final long serialVersionUID = 1547272262911742983L;
    private BaseEnemy target;

    public BarrageAction(BaseEnemy target) {
        this.target = target;
    }

    public void start(final BasePet pet) {
        if (this.target.canDone(pet)) {
            DialogService.confirm(this.target.getName(), "力量 : " + this.target.getStrength() + "\r\n速度 : " + this.target.getSpeed() + "\r\n灵力 : " + this.target.getMagic() + "\r\n\r\n" + this.target.getStartMessage(), "开始", "取消", new DialogService.CallBack() { // from class: k.p.action.BarrageAction.1
                @Override // k.p.services.DialogService.CallBack
                public void onReturn(boolean retVal) {
                    if (retVal) {
                        pet.changeEnergy(-10);
                        BarrageService.newBarrage(pet, BarrageAction.this.target);
                    }
                }
            });
        } else {
            DialogService.alert(this.target.getName(), this.target.getStartMessage(), "取消", (DialogService.CallBack) null);
        }
    }

    @Override // k.p.action.Action
    public boolean canDone(BasePet pet, T target) {
        return pet.getEnergy() > 30;
    }

    @Override // k.p.action.Action
    public String getActionTag() {
        return actionTag;
    }

    @Override // k.p.action.Action
    public String getActionDescription() {
        return actionDescription;
    }

    @Override // k.p.action.BaseImmediatelyAction
    protected String getCannotDoneMsg() {
        return "宠物太累了,打不了弹幕";
    }

    @Override // k.p.action.BaseImmediatelyAction
    protected String getStartActionMsg() {
        return "开始打弹幕!";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // k.p.action.BaseImmediatelyAction
    public void onDone(BasePet pet, T target) {
    }
}
