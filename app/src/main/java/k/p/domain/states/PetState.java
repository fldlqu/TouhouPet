package k.p.domain.states;

import k.p.action.BaseStateAction;
import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public interface PetState {
    boolean addCurrentDuration(int i);

    BaseStateAction<?> getAction();

    int getCurrentDuration();

    int getMaxDuration();

    BasePet getPet();

    String getStateDoingDescription();

    String getStateName();

    String getStateTag();

    int getWeight();

    void onEnd();

    void onFinish();

    void onInterrupt();

    void onPause();

    void onResume();

    void onStart();

    void setAction(BaseStateAction<?> baseStateAction);
}
