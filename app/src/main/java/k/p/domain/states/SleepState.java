package k.p.domain.states;

import k.p.domain.BasePet;
import k.p.services.AnimationService;

/* JADX INFO: loaded from: classes.dex */
public class SleepState extends BasePetState {
    private static final long serialVersionUID = 1;
    private static final String stateDescription = "睡觉中...";
    private static final String stateName = "睡觉";
    private static final String stateTag = "sleep";

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
        BasePet pet = getPet();
        pet.setChangeEnergyValue(2);
        pet.setDecreaseRepletionDegreeTime(pet.getDefaultDecreaseRDTime() * 4);
        pet.setDecreaseDrinkDegreeTime(pet.getDefaultDecreaseDDTime() * 4);
        AnimationService.requestChangeAnimation(AnimationService.getRandomAnimationByType("SLEEP"));
    }

    @Override // k.p.domain.states.BasePetState, k.p.domain.states.PetState
    public void onEnd() {
        BasePet pet = getPet();
        pet.setChangeEnergyValue(-1);
        pet.setDecreaseRepletionDegreeTime(pet.getDefaultDecreaseRDTime());
        pet.setDecreaseDrinkDegreeTime(pet.getDefaultDecreaseDDTime());
    }
}
