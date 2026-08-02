package k.p.action;

import k.p.domain.BasePet;
import k.p.domain.ReturnStatus;
import k.p.services.DialogService;

/* JADX INFO: loaded from: classes.dex */
public abstract class BaseStateAction<T> extends BaseAction<T> {
    protected int maxDuration;

    protected abstract String getCannotDoneMsg();

    protected abstract int getRequestWeight();

    protected abstract String getStartActionMsg();

    @Override // k.p.action.Action
    public void setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration;
    }

    @Override // k.p.action.Action
    public int getMaxDuration() {
        return this.maxDuration;
    }

    @Override // k.p.action.Action
    public void onStart(BasePet pet, T target) {
    }

    @Override // k.p.action.Action
    public void onFinish() {
    }

    @Override // k.p.action.Action
    public ReturnStatus<String> doAction(final BasePet pet, final T target) {
        if (canDone(pet, target)) {
            getState().setAction(this);
            if (pet.requestChangeState(getState(), getRequestWeight())) {
                onStart(pet, target);
                return new ReturnStatus<>(true, getStartActionMsg());
            }
            DialogService.confirm(getState().getStateName(), "当前正在" + pet.getCurrentState().getStateDoingDescription() + "\r\n是否要继续" + getState().getStateName(), "继续", "取消", new DialogService.CallBack() { // from class: k.p.action.BaseStateAction.1
                /* JADX WARN: Multi-variable type inference failed */
                @Override // k.p.services.DialogService.CallBack
                public void onReturn(boolean retVal) {
                    if (retVal) {
                        BaseStateAction.this.onStart(pet, target);
                        BaseStateAction.this.getState().setAction(BaseStateAction.this);
                        pet.requestChangeState(BaseStateAction.this.getState(), BasePet.MAX_LEVEL);
                    }
                }
            });
            return new ReturnStatus<>(true, getStartActionMsg());
        }
        return new ReturnStatus<>(false, getCannotDoneMsg());
    }
}
