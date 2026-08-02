package k.p.action;

import java.io.Serializable;
import k.p.domain.BasePet;
import k.p.domain.states.BasePetState;
import k.p.services.DialogService;
import k.p.services.PetService;
import k.p.services.StateService;

/* JADX INFO: loaded from: classes.dex */
public class StudyAction<T> extends BaseStateAction<T> {
    private static final String actionDescription = "学习";
    private static final String actionTag = "study";
    private static final long serialVersionUID = 3019838837214048124L;
    private BaseStudyInfo info;

    public static abstract class BaseStudyInfo implements StudyInfo, Serializable {
    }

    public interface StudyInfo {
        boolean canDone(BasePet basePet);

        String getDoneMessage();

        int getMaxDuration();

        String getName();

        String getStartMessage();

        void onDone(BasePet basePet);
    }

    public StudyAction(BaseStudyInfo info) {
        this.info = info;
        this.maxDuration = info.getMaxDuration();
    }

    public void study(final BasePet pet) {
        if (this.info.canDone(pet)) {
            DialogService.confirm(this.info.getName(), this.info.getStartMessage(), "开始" + this.info.getName(), "取消", new DialogService.CallBack() { // from class: k.p.action.StudyAction.1
                @Override // k.p.services.DialogService.CallBack
                public void onReturn(boolean retVal) {
                    if (retVal) {
                        StudyAction.this.doAction(pet, null);
                    }
                }
            });
        } else {
            DialogService.alert(this.info.getName(), this.info.getStartMessage(), "取消", (DialogService.CallBack) null);
        }
    }

    @Override // k.p.action.Action
    public BasePetState getState() {
        return StateService.STUDY;
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
        return "宠物太累了,无法进行学习";
    }

    @Override // k.p.action.BaseStateAction
    protected String getStartActionMsg() {
        return "开始学习...";
    }

    @Override // k.p.action.BaseStateAction
    protected int getRequestWeight() {
        return 1;
    }

    @Override // k.p.action.BaseStateAction, k.p.action.Action
    public void onFinish() {
        super.onFinish();
        DialogService.alert("学习完成", this.info.getDoneMessage());
        this.info.onDone(PetService.pet);
    }
}
