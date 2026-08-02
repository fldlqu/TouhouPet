package k.p.services

import k.p.listener.OnPetPropertyChangeListener
import k.p.listener.PetPropertyChangeEvent
import java.util.ArrayList

class ListenerService private constructor() {
    private var ppcListenerList: MutableList<OnPetPropertyChangeListener> = ArrayList()

    companion object {
        private var instance: ListenerService? = null

        @JvmStatic
        fun init() {
            instance = ListenerService()
            instance!!.ppcListenerList = ArrayList()
        }

        @JvmStatic
        fun registerListener(listener: OnPetPropertyChangeListener) {
            instance!!.ppcListenerList.add(listener)
        }

        @JvmStatic
        fun notifyListener(event: PetPropertyChangeEvent) {
            for (listener in instance!!.ppcListenerList) {
                listener.onPetPropertyChange(event)
            }
        }

        @JvmStatic
        fun release() {
            instance = null
        }
    }
}