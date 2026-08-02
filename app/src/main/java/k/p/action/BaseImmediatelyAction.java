package k.p.action;

import k.p.domain.BasePet;
import k.p.domain.ReturnStatus;
import k.p.domain.states.BasePetState;
import k.p.services.DialogService;

/* JADX INFO: loaded from: classes.dex */
public abstract class BaseImmediatelyAction<T> extends BaseAction<T> {
    protected abstract String getCannotDoneMsg();

    protected abstract String getStartActionMsg();

    protected abstract void onDone(BasePet basePet, T t);

    @Override // k.p.action.Action
    public BasePetState getState() {
        return null;
    }

    @Override // k.p.action.Action
    public void onFinish() {
    }

    @Override // k.p.action.Action
    public void onStart(BasePet pet, T target) {
        onDone(pet, target);
    }

    @Override // k.p.action.Action
    public ReturnStatus<String> doAction(final BasePet pet, final T target) {
        if (!canDone(pet, target)) {
            return new ReturnStatus<>(false, getCannotDoneMsg());
        }
        DialogService.confirm(getActionDescription(), "确定要" + getActionDescription() + "吗?", "确定", "取消", new DialogService.CallBack() { // from class: k.p.action.BaseImmediatelyAction.1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // k.p.services.DialogService.CallBack
            public void onReturn(boolean retVal) {
                if (retVal) {
                    BaseImmediatelyAction.this.onStart(pet, target);
                }
            }
        });
        return new ReturnStatus<>(true, getStartActionMsg());
    }

    @Override // k.p.action.Action
    public void setMaxDuration(int maxDuration) {
    }

    @Override // k.p.action.Action
    public int getMaxDuration() {
        return 0;
    }
}
