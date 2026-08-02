package k.p.action;

import k.p.domain.BasePet;
import k.p.domain.states.SleepState;
import k.p.services.StateService;

/* JADX INFO: loaded from: classes.dex */
public class WakeUpAction<T> extends BaseImmediatelyAction<T> {
    private static final String actionDescription = "起床";
    private static final String actionTag = "wakeup";
    private static final long serialVersionUID = -997754806468874847L;

    @Override // k.p.action.Action
    public boolean canDone(BasePet pet, T target) {
        return pet.getCurrentState() instanceof SleepState;
    }

    @Override // k.p.action.BaseImmediatelyAction
    protected void onDone(BasePet pet, T target) {
        pet.requestChangeState(StateService.ACTIVE, BasePet.MAX_LEVEL);
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
        return "宠物没有在睡觉...";
    }

    @Override // k.p.action.BaseImmediatelyAction
    protected String getStartActionMsg() {
        return "起床了";
    }
}
