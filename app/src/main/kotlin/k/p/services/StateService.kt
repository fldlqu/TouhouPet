package k.p.services

import k.p.domain.states.ActiveState
import k.p.domain.states.BasePetState
import k.p.domain.states.DeadState
import k.p.domain.states.ForageState
import k.p.domain.states.SearchState
import k.p.domain.states.SleepState
import k.p.domain.states.StudyState
import k.p.domain.states.WorkState

object StateService {
    @JvmField
    var ACTIVE: ActiveState? = null
    @JvmField
    var DEAD: DeadState? = null
    @JvmField
    var FORAGE: ForageState? = null
    @JvmField
    var SEARCH: SearchState? = null
    @JvmField
    var SLEEP: SleepState? = null
    @JvmField
    var STUDY: StudyState? = null
    @JvmField
    var WORK: WorkState? = null
    @JvmField
    var stateMap: MutableMap<String, BasePetState>? = null

    @JvmStatic
    fun init() {
        stateMap = HashMap()
        SLEEP = SleepState()
        ACTIVE = ActiveState()
        FORAGE = ForageState()
        SEARCH = SearchState()
        STUDY = StudyState()
        WORK = WorkState()
        DEAD = DeadState()
        registerState(SLEEP!!)
        registerState(ACTIVE!!)
        registerState(FORAGE!!)
        registerState(SEARCH!!)
        registerState(STUDY!!)
        registerState(WORK!!)
        registerState(DEAD!!)
    }

    @JvmStatic
    fun registerState(state: BasePetState) {
        stateMap!![state.getStateTag()] = state
    }

    @JvmStatic
    fun findStateByTag(stateTag: String): BasePetState? {
        return stateMap?.get(stateTag)
    }

    @JvmStatic
    fun release() {
        SLEEP = null
        ACTIVE = null
        FORAGE = null
        SEARCH = null
        STUDY = null
        WORK = null
        DEAD = null
        stateMap = null
    }
}