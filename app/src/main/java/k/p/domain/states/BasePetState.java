package k.p.domain.states;

import java.io.Serializable;
import k.p.action.BaseStateAction;
import k.p.domain.BasePet;
import k.p.services.PetService;

/* JADX INFO: loaded from: classes.dex */
public abstract class BasePetState implements PetState, Serializable {
    private static final long serialVersionUID = 1;
    protected BaseStateAction<?> action;
    protected int currentDuration;

    @Override // k.p.domain.states.PetState
    public BaseStateAction<?> getAction() {
        return this.action;
    }

    @Override // k.p.domain.states.PetState
    public void setAction(BaseStateAction<?> action) {
        this.action = action;
    }

    @Override // k.p.domain.states.PetState
    public int getCurrentDuration() {
        return this.currentDuration;
    }

    @Override // k.p.domain.states.PetState
    public int getMaxDuration() {
        return this.action.getMaxDuration();
    }

    @Override // k.p.domain.states.PetState
    public boolean addCurrentDuration(int time) {
        this.currentDuration += time;
        if (this.currentDuration <= getMaxDuration()) {
            return false;
        }
        onFinish();
        onEnd();
        return true;
    }

    @Override // k.p.domain.states.PetState
    public void onPause() {
    }

    @Override // k.p.domain.states.PetState
    public void onResume() {
    }

    @Override // k.p.domain.states.PetState
    public void onStart() {
    }

    @Override // k.p.domain.states.PetState
    public void onFinish() {
        if (this.action != null) {
            this.action.onFinish();
        }
    }

    @Override // k.p.domain.states.PetState
    public void onEnd() {
        this.currentDuration = 0;
    }

    @Override // k.p.domain.states.PetState
    public void onInterrupt() {
    }

    @Override // k.p.domain.states.PetState
    public BasePet getPet() {
        return PetService.pet;
    }
}
