package k.p.action;

import k.p.domain.BasePet;
import k.p.domain.ReturnStatus;
import k.p.domain.states.BasePetState;
import k.p.domain.states.SleepState;
import k.p.services.StateService;

/* JADX INFO: loaded from: classes.dex */
public class SleepAction<T> extends BaseStateAction<T> {
    private static final String actionDescription = "睡觉";
    private static final String actionTag = "sleep";
    private static final long serialVersionUID = -7291877758330086137L;

    public SleepAction() {
        this.maxDuration = Integer.MAX_VALUE;
    }

    @Override // k.p.action.Action
    public boolean canDone(BasePet pet, T target) {
        return true;
    }

    @Override // k.p.action.BaseStateAction, k.p.action.Action
    public ReturnStatus<String> doAction(BasePet pet, T target) {
        return !(pet.getCurrentState() instanceof SleepState) ? super.doAction(pet, target) : new ReturnStatus<>(false, "宠物已经睡着了...");
    }

    @Override // k.p.action.Action
    public String getActionTag() {
        return actionTag;
    }

    @Override // k.p.action.Action
    public String getActionDescription() {
        return actionDescription;
    }

    @Override // k.p.action.Action
    public BasePetState getState() {
        return StateService.SLEEP;
    }

    @Override // k.p.action.BaseStateAction
    protected String getCannotDoneMsg() {
        return null;
    }

    @Override // k.p.action.BaseStateAction
    protected String getStartActionMsg() {
        return "开始睡觉";
    }

    @Override // k.p.action.BaseStateAction
    protected int getRequestWeight() {
        return 1;
    }
}
