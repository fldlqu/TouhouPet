package k.p.action;

import java.io.Serializable;
import k.p.domain.BasePet;
import k.p.domain.states.BasePetState;
import k.p.services.DialogService;
import k.p.services.PetService;
import k.p.services.StateService;

/* JADX INFO: loaded from: classes.dex */
public class WorkAction<T> extends BaseStateAction<T> {
    private static final String actionDescription = "工作";
    private static final String actionTag = "work";
    private static final long serialVersionUID = 8200292717510943251L;
    private BaseWorkInfo info;

    public static abstract class BaseWorkInfo implements WorkInfo, Serializable {
    }

    public interface WorkInfo {
        boolean canDone(BasePet basePet);

        String getDoneMessage();

        int getMaxDuration();

        String getName();

        String getStartMessage();

        void onDone(BasePet basePet);
    }

    public WorkAction(BaseWorkInfo info) {
        this.info = info;
        this.maxDuration = info.getMaxDuration();
    }

    public void work(final BasePet pet) {
        if (this.info.canDone(pet)) {
            DialogService.confirm(this.info.getName(), this.info.getStartMessage(), "开始" + this.info.getName(), "取消", new DialogService.CallBack() { // from class: k.p.action.WorkAction.1
                @Override // k.p.services.DialogService.CallBack
                public void onReturn(boolean retVal) {
                    if (retVal) {
                        WorkAction.this.doAction(pet, null);
                    }
                }
            });
        } else {
            DialogService.alert(this.info.getName(), this.info.getStartMessage(), "取消", (DialogService.CallBack) null);
        }
    }

    @Override // k.p.action.Action
    public BasePetState getState() {
        return StateService.WORK;
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

    @Override // k.p.action.BaseStateAction
    protected String getCannotDoneMsg() {
        return "宠物太累了,无法进行" + this.info.getName();
    }

    @Override // k.p.action.BaseStateAction
    protected String getStartActionMsg() {
        return "开始" + this.info.getName() + "...";
    }

    @Override // k.p.action.BaseStateAction
    protected int getRequestWeight() {
        return 1;
    }

    @Override // k.p.action.BaseStateAction, k.p.action.Action
    public void onFinish() {
        super.onFinish();
        DialogService.alert(String.valueOf(this.info.getName()) + "完成", this.info.getDoneMessage());
        this.info.onDone(PetService.pet);
    }
}
