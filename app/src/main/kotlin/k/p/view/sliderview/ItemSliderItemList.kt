package k.p.view.sliderview

import k.p.item.BaseEatableItem
import k.p.item.Item
import k.p.item.drink.Drink
import k.p.listener.ItemChangeEvent
import k.p.listener.OnItemChangeListener
import k.p.services.DialogService
import k.p.services.ItemService

class ItemSliderItemList(sv: SliderView, sc: SliderCanvas) : SliderItemList(sv, sc) {
    private var drinkListOpen = true
    private var foodListOpen = true
    private var otherListOpen = true
    private var specialFoodListOpen = true

    override fun init() {
        super.init()
        ItemService.registerItemListener(object : OnItemChangeListener {
            override fun onItemChange(event: ItemChangeEvent) {
                refreshItemList()
            }
        })
        refreshItemList()
    }

    fun refreshItemList() {
        if (ItemService.getAllItems() != null) {
            synchronized(this) {
                clearSliderItemView()
                val foodList = ArrayList<Item>()
                val drinkList = ArrayList<Item>()
                val specialFoodList = ArrayList<Item>()
                val otherList = ArrayList<Item>()
                for (item in ItemService.getAllItems()) {
                    if (item.getItemType() == "食物") {
                        foodList.add(item)
                    } else if (item.getItemType() == "饮料") {
                        drinkList.add(item)
                    } else if (item.getItemType() == "特殊") {
                        specialFoodList.add(item)
                    } else {
                        otherList.add(item)
                    }
                }
                if (foodList.size > 0) {
                    addSliderItemView(object : BaseSliderTitleTextButton(sliderView, "食物") {
                        override fun onClick() {
                            foodListOpen = !foodListOpen
                            refreshItemList()
                        }
                    })
                    if (foodListOpen) {
                        for (item in foodList) {
                            addSliderItemView(ItemButton(sliderView, item))
                        }
                    }
                }
                if (drinkList.size > 0) {
                    addSliderItemView(object : BaseSliderTitleTextButton(sliderView, "饮料") {
                        override fun onClick() {
                            drinkListOpen = !drinkListOpen
                            refreshItemList()
                        }
                    })
                    if (drinkListOpen) {
                        for (item in drinkList) {
                            addSliderItemView(ItemButton(sliderView, item))
                        }
                    }
                }
                if (specialFoodList.size > 0) {
                    addSliderItemView(object : BaseSliderTitleTextButton(sliderView, "特殊") {
                        override fun onClick() {
                            specialFoodListOpen = !specialFoodListOpen
                            refreshItemList()
                        }
                    })
                    if (specialFoodListOpen) {
                        for (item in specialFoodList) {
                            addSliderItemView(ItemButton(sliderView, item))
                        }
                    }
                }
                if (otherList.size > 0) {
                    addSliderItemView(object : BaseSliderTitleTextButton(sliderView, "其他") {
                        override fun onClick() {
                            otherListOpen = !otherListOpen
                            refreshItemList()
                        }
                    })
                    if (otherListOpen) {
                        for (item in otherList) {
                            addSliderItemView(ItemButton(sliderView, item))
                        }
                    }
                }
                val returnButton = ReturnButton(sliderView)
                returnButton.init()
                addSliderItemView(returnButton)
            }
        }
    }

    inner class ItemButton(sliderView: SliderView, var item: Item) :
        BaseSliderTextButton(sliderView, item.getName()) {

        override fun onClick() {
            if (item.canUse()) {
                val name = item.getName()
                val itemDescription = item.getItemDescription()
                val str = when (item) {
                    is Drink -> "喝掉"
                    is BaseEatableItem -> "吃掉"
                    else -> "使用"
                }
                DialogService.confirm(name, itemDescription, str, "返回",
                    object : DialogService.CallBack {
                        override fun onReturn(retVal: Boolean) {
                            if (retVal) {
                                item.use(sliderView.mainService!!.pet!!!!, null)
                            }
                        }
                    })
            }
        }
    }
}