package k.p.action

import k.p.domain.BasePet
import k.p.domain.ReturnStatus
import k.p.domain.states.BasePetState

interface Action<T> {
    fun canDone(basePet: BasePet, t: T): Boolean
    fun doAction(basePet: BasePet, t: T): ReturnStatus<*>?
    fun getActionDescription(): String
    fun getActionTag(): String
    fun getMaxDuration(): Int
    fun getState(): BasePetState
    fun onFinish()
    fun onStart(basePet: BasePet, t: T)
    fun setMaxDuration(i: Int)
}
