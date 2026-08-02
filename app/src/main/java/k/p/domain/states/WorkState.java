package k.p.domain.states;

import k.p.services.AnimationService;

/* JADX INFO: loaded from: classes.dex */
public class WorkState extends BasePetState {
    private static final long serialVersionUID = 1765124442221734962L;
    private static final String stateDescription = "工作中...";
    private static final String stateName = "工作";
    private static final String stateTag = "work";

    @Override // k.p.domain.states.PetState
    public int getWeight() {
        return 3;
    }

    @Override // k.p.domain.states.PetState
    public String getStateDoingDescription() {
        return "工作中... " + ((getCurrentDuration() / 1000) / 60) + "/" + ((getMaxDuration() / 1000) / 60) + "分钟";
    }

    @Override // k.p.domain.states.PetState
    public String getStateName() {
        return stateName;
    }

    @Override // k.p.domain.states.PetState
    public String getStateTag() {
        return stateTag;
    }

    @Override // k.p.domain.states.BasePetState, k.p.domain.states.PetState
    public void onStart() {
        AnimationService.requestChangeAnimation(AnimationService.getRandomAnimationByType("WORK"));
    }
}
