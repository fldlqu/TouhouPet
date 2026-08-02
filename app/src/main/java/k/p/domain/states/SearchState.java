package k.p.domain.states;

import k.p.services.AnimationService;

/* JADX INFO: loaded from: classes.dex */
public class SearchState extends BasePetState {
    private static final long serialVersionUID = 2432007018387996677L;
    private static final String stateDescription = "探索中...";
    private static final String stateName = "探索";
    private static final String stateTag = "search";

    @Override // k.p.domain.states.PetState
    public int getWeight() {
        return 3;
    }

    @Override // k.p.domain.states.BasePetState, k.p.domain.states.PetState
    public int getMaxDuration() {
        return 600000;
    }

    @Override // k.p.domain.states.PetState
    public String getStateDoingDescription() {
        return "探索中... " + ((getCurrentDuration() / 1000) / 60) + "/" + ((getMaxDuration() / 1000) / 60) + "分钟";
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
    public void onFinish() {
        getPet().getCurrentLocation().onFinishSearch();
    }

    @Override // k.p.domain.states.BasePetState, k.p.domain.states.PetState
    public void onStart() {
        getPet().getCurrentLocation().onStartSearch();
        AnimationService.requestChangeAnimation(AnimationService.getRandomAnimationByType("SEARCH"));
    }
}
