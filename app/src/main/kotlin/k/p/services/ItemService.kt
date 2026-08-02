package k.p.services

import k.p.item.BaseItem
import k.p.listener.ItemChangeEvent
import k.p.listener.OnItemChangeListener
import java.util.ArrayList

class ItemService private constructor() {
    private var listenerList: MutableList<OnItemChangeListener> = ArrayList()

    companion object {
        private var instance: ItemService? = null

        @JvmStatic
        fun init() {
            instance = ItemService()
            instance!!.listenerList = ArrayList()
        }

        @JvmStatic
        fun getAllItems(): MutableList<BaseItem> {
            return PetService.pet!!.getAllItems()
        }

        @JvmStatic
        fun getItemsByType(itemType: String): MutableList<BaseItem> {
            val list = ArrayList<BaseItem>()
            for (item in PetService.pet!!.getAllItems()) {
                if (itemType.equals(item.getItemType())) {
                    list.add(item)
                }
            }
            return list
        }

        @JvmStatic
        fun getItemsByName(itemName: String): MutableList<BaseItem> {
            val list = ArrayList<BaseItem>()
            for (item in PetService.pet!!.getAllItems()) {
                if (itemName.equals(item.getName())) {
                    list.add(item)
                }
            }
            return list
        }

        @JvmStatic
        fun addItem(item: BaseItem) {
            item.onGet()
            PetService.pet!!.getAllItems().add(item)
            if (instance!!.listenerList.size > 0) {
                val event = ItemChangeEvent(item, 0)
                for (listener in instance!!.listenerList) {
                    listener.onItemChange(event)
                }
            }
        }

        @JvmStatic
        fun useItem(item: BaseItem) {
            PetService.pet!!.getAllItems().remove(item)
            if (instance!!.listenerList.size > 0) {
                val event = ItemChangeEvent(item, 2)
                for (listener in instance!!.listenerList) {
                    listener.onItemChange(event)
                }
            }
        }

        @JvmStatic
        fun dropItem(item: BaseItem) {
            PetService.pet!!.getAllItems().remove(item)
            if (instance!!.listenerList.size > 0) {
                val event = ItemChangeEvent(item, 1)
                for (listener in instance!!.listenerList) {
                    listener.onItemChange(event)
                }
            }
        }

        @JvmStatic
        fun registerItemListener(listener: OnItemChangeListener) {
            instance!!.listenerList.add(listener)
        }

        @JvmStatic
        fun release() {
            instance = null
        }
    }
}