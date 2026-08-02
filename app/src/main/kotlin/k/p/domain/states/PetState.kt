package k.p.domain.states

import k.p.action.BaseStateAction
import k.p.domain.BasePet

interface PetState {
    fun addCurrentDuration(i: Int): Boolean
    fun getAction(): BaseStateAction<*>?
    fun getCurrentDuration(): Int
    fun getMaxDuration(): Int
    fun getPet(): BasePet
    fun getStateDoingDescription(): String
    fun getStateName(): String
    fun getStateTag(): String
    fun getWeight(): Int
    fun onEnd()
    fun onFinish()
    fun onInterrupt()
    fun onPause()
    fun onResume()
    fun onStart()
    fun setAction(baseStateAction: BaseStateAction<*>?)
}
