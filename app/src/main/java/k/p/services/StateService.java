package k.p.services;

import java.util.HashMap;
import java.util.Map;
import k.p.domain.states.ActiveState;
import k.p.domain.states.BasePetState;
import k.p.domain.states.DeadState;
import k.p.domain.states.ForageState;
import k.p.domain.states.SearchState;
import k.p.domain.states.SleepState;
import k.p.domain.states.StudyState;
import k.p.domain.states.WorkState;

/* JADX INFO: loaded from: classes.dex */
public class StateService {
    public static ActiveState ACTIVE;
    public static DeadState DEAD;
    public static ForageState FORAGE;
    public static SearchState SEARCH;
    public static SleepState SLEEP;
    public static StudyState STUDY;
    public static WorkState WORK;
    public static Map<String, BasePetState> stateMap;

    public static void init() {
        stateMap = new HashMap();
        SLEEP = new SleepState();
        ACTIVE = new ActiveState();
        FORAGE = new ForageState();
        SEARCH = new SearchState();
        STUDY = new StudyState();
        WORK = new WorkState();
        DEAD = new DeadState();
        registerState(SLEEP);
        registerState(ACTIVE);
        registerState(FORAGE);
        registerState(SEARCH);
        registerState(STUDY);
        registerState(WORK);
        registerState(DEAD);
    }

    public static void registerState(BasePetState state) {
        stateMap.put(state.getStateTag(), state);
    }

    public static BasePetState findStateByTag(String stateTag) {
        return stateMap.get(stateTag);
    }

    public static void release() {
        SLEEP = null;
        ACTIVE = null;
        FORAGE = null;
        SEARCH = null;
        STUDY = null;
        WORK = null;
        DEAD = null;
        stateMap = null;
    }
}
