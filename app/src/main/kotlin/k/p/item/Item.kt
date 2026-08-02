package k.p.item

import k.p.domain.BasePet
import k.p.domain.ReturnStatus

interface Item {
    fun canUse(): Boolean
    fun getItemDescription(): String
    fun getItemType(): String
    fun getName(): String
    fun onDestroy(i: Int)
    fun onGet()
    fun use(basePet: BasePet, obj: Any?): ReturnStatus<*>?

    companion object {
        const val DROP = 0
        const val SOLD = 1
        const val USE = 3
    }
}
