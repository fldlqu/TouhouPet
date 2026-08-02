package k.p.action;

import k.p.domain.BasePet;
import k.p.domain.states.BasePetState;
import k.p.location.Location;
import k.p.services.StateService;

/* JADX INFO: loaded from: classes.dex */
public class SearchAction<T extends Location> extends BaseStateAction<T> {
    private static final String actionDescription = "探索";
    private static final String actionTag = "search";
    private static final long serialVersionUID = 1603417301804317789L;

    public SearchAction() {
        this.maxDuration = 600000;
    }

    @Override // k.p.action.Action
    public BasePetState getState() {
        return StateService.SEARCH;
    }

    @Override // k.p.action.Action
    public boolean canDone(BasePet pet, Location target) {
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
        return "宠物太累了,无法进行探索";
    }

    @Override // k.p.action.BaseStateAction
    protected String getStartActionMsg() {
        return "开始探索...";
    }

    @Override // k.p.action.BaseStateAction
    protected int getRequestWeight() {
        return 1;
    }

    @Override // k.p.action.BaseStateAction, k.p.action.Action
    public void onFinish() {
        super.onFinish();
    }
}
