package k.p.listener

import k.p.item.Item

class ItemChangeEvent(item: Item, type: Int) {
    var item: Item = item
        private set
    var type: Int = type
        private set

    companion object {
        const val GET = 0
        const val DROP = 1
        const val USE = 2
    }
}
