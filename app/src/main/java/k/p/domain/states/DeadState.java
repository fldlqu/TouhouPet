package k.p.domain.states;

import k.p.services.AnimationService;

/* JADX INFO: loaded from: classes.dex */
public class DeadState extends BasePetState {
    private static final long serialVersionUID = 1;
    private static final String stateDescription = "死亡";
    private static final String stateName = "死亡";
    private static final String stateTag = "dead";

    @Override // k.p.domain.states.PetState
    public int getWeight() {
        return Math.max((20 - getPet().getEnergy()) / 2, 1);
    }

    @Override // k.p.domain.states.BasePetState, k.p.domain.states.PetState
    public int getMaxDuration() {
        return Integer.MAX_VALUE;
    }

    @Override // k.p.domain.states.PetState
    public String getStateDoingDescription() {
        return "死亡";
    }

    @Override // k.p.domain.states.PetState
    public String getStateTag() {
        return stateTag;
    }

    @Override // k.p.domain.states.PetState
    public String getStateName() {
        return "死亡";
    }

    @Override // k.p.domain.states.BasePetState, k.p.domain.states.PetState
    public void onStart() {
        AnimationService.requestChangeAnimation(AnimationService.getRandomAnimationByType("SLEEP"));
    }

    @Override // k.p.domain.states.BasePetState, k.p.domain.states.PetState
    public void onEnd() {
    }
}
