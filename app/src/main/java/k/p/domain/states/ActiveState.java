package k.p.domain.states;

import k.p.services.AnimationService;

/* JADX INFO: loaded from: classes.dex */
public class ActiveState extends BasePetState {
    private static final long serialVersionUID = 1;
    private static final String stateDescription = "闲逛中...";
    private static final String stateName = "闲逛";
    private static final String stateTag = "active";

    @Override // k.p.domain.states.PetState
    public int getWeight() {
        return 0;
    }

    @Override // k.p.domain.states.BasePetState, k.p.domain.states.PetState
    public int getMaxDuration() {
        return Integer.MAX_VALUE;
    }

    @Override // k.p.domain.states.PetState
    public String getStateDoingDescription() {
        return stateDescription;
    }

    @Override // k.p.domain.states.PetState
    public String getStateTag() {
        return stateTag;
    }

    @Override // k.p.domain.states.PetState
    public String getStateName() {
        return stateName;
    }

    @Override // k.p.domain.states.BasePetState, k.p.domain.states.PetState
    public void onStart() {
        AnimationService.requestChangeAnimation(AnimationService.getRandomAnimationByType("ACTIVE"));
    }
}
