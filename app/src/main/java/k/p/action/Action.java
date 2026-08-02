package k.p.action;

import k.p.domain.BasePet;
import k.p.domain.ReturnStatus;
import k.p.domain.states.BasePetState;

/* JADX INFO: loaded from: classes.dex */
public interface Action<T> {
    boolean canDone(BasePet basePet, T t);

    ReturnStatus<?> doAction(BasePet basePet, T t);

    String getActionDescription();

    String getActionTag();

    int getMaxDuration();

    BasePetState getState();

    void onFinish();

    void onStart(BasePet basePet, T t);

    void setMaxDuration(int i);
}
