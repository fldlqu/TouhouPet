package k.p.view.sliderview;

import java.util.ArrayList;
import java.util.List;
import k.p.item.BaseEatableItem;
import k.p.item.Item;
import k.p.item.drink.Drink;
import k.p.listener.ItemChangeEvent;
import k.p.listener.OnItemChangeListener;
import k.p.services.DialogService;
import k.p.services.ItemService;

/* JADX INFO: loaded from: classes.dex */
public class ItemSliderItemList extends SliderItemList {
    private boolean drinkListOpen;
    private boolean foodListOpen;
    private boolean otherListOpen;
    private boolean specialFoodListOpen;

    public ItemSliderItemList(SliderView sliderView, SliderCanvas sc) {
        super(sliderView, sc);
        this.foodListOpen = true;
        this.drinkListOpen = true;
        this.specialFoodListOpen = true;
        this.otherListOpen = true;
    }

    @Override // k.p.view.sliderview.SliderItemList
    public void init() {
        super.init();
        ItemService.registerItemListener(new OnItemChangeListener() { // from class: k.p.view.sliderview.ItemSliderItemList.1
            @Override // k.p.listener.OnItemChangeListener
            public void onItemChange(ItemChangeEvent event) {
                ItemSliderItemList.this.refreshItemList();
            }
        });
        refreshItemList();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshItemList() {
        if (ItemService.getAllItems() != null) {
            synchronized (this) {
                clearSliderItemView();
                List<Item> foodList = new ArrayList<>();
                List<Item> drinkList = new ArrayList<>();
                List<Item> specialFoodList = new ArrayList<>();
                List<Item> otherList = new ArrayList<>();
                for (Item item : ItemService.getAllItems()) {
                    if (item.getItemType().equals("食物")) {
                        foodList.add(item);
                    } else if (item.getItemType().equals("饮料")) {
                        drinkList.add(item);
                    } else if (item.getItemType().equals("特殊")) {
                        specialFoodList.add(item);
                    } else {
                        otherList.add(item);
                    }
                }
                if (foodList.size() > 0) {
                    addSliderItemView(new BaseSliderTitleTextButton(this.sliderView, "食物") { // from class: k.p.view.sliderview.ItemSliderItemList.2
                        @Override // k.p.view.sliderview.BaseSliderTitleTextButton, k.p.view.sliderview.SliderItemView
                        public void onClick() {
                            if (ItemSliderItemList.this.foodListOpen) {
                                ItemSliderItemList.this.foodListOpen = false;
                                ItemSliderItemList.this.refreshItemList();
                            } else {
                                ItemSliderItemList.this.foodListOpen = true;
                                ItemSliderItemList.this.refreshItemList();
                            }
                        }
                    });
                    if (this.foodListOpen) {
                        for (Item item2 : foodList) {
                            addSliderItemView(new ItemButton(this.sliderView, item2));
                        }
                    }
                }
                if (drinkList.size() > 0) {
                    addSliderItemView(new BaseSliderTitleTextButton(this.sliderView, "饮料") { // from class: k.p.view.sliderview.ItemSliderItemList.3
                        @Override // k.p.view.sliderview.BaseSliderTitleTextButton, k.p.view.sliderview.SliderItemView
                        public void onClick() {
                            if (ItemSliderItemList.this.drinkListOpen) {
                                ItemSliderItemList.this.drinkListOpen = false;
                                ItemSliderItemList.this.refreshItemList();
                            } else {
                                ItemSliderItemList.this.drinkListOpen = true;
                                ItemSliderItemList.this.refreshItemList();
                            }
                        }
                    });
                    if (this.drinkListOpen) {
                        for (Item item3 : drinkList) {
                            addSliderItemView(new ItemButton(this.sliderView, item3));
                        }
                    }
                }
                if (specialFoodList.size() > 0) {
                    addSliderItemView(new BaseSliderTitleTextButton(this.sliderView, "特殊") { // from class: k.p.view.sliderview.ItemSliderItemList.4
                        @Override // k.p.view.sliderview.BaseSliderTitleTextButton, k.p.view.sliderview.SliderItemView
                        public void onClick() {
                            if (ItemSliderItemList.this.specialFoodListOpen) {
                                ItemSliderItemList.this.specialFoodListOpen = false;
                                ItemSliderItemList.this.refreshItemList();
                            } else {
                                ItemSliderItemList.this.specialFoodListOpen = true;
                                ItemSliderItemList.this.refreshItemList();
                            }
                        }
                    });
                    if (this.specialFoodListOpen) {
                        for (Item item4 : specialFoodList) {
                            addSliderItemView(new ItemButton(this.sliderView, item4));
                        }
                    }
                }
                if (otherList.size() > 0) {
                    addSliderItemView(new BaseSliderTitleTextButton(this.sliderView, "其他") { // from class: k.p.view.sliderview.ItemSliderItemList.5
                        @Override // k.p.view.sliderview.BaseSliderTitleTextButton, k.p.view.sliderview.SliderItemView
                        public void onClick() {
                            if (ItemSliderItemList.this.otherListOpen) {
                                ItemSliderItemList.this.otherListOpen = false;
                                ItemSliderItemList.this.refreshItemList();
                            } else {
                                ItemSliderItemList.this.otherListOpen = true;
                                ItemSliderItemList.this.refreshItemList();
                            }
                        }
                    });
                    if (this.otherListOpen) {
                        for (Item item5 : otherList) {
                            addSliderItemView(new ItemButton(this.sliderView, item5));
                        }
                    }
                }
                ReturnButton returnButton = new ReturnButton(this.sliderView);
                returnButton.init();
                addSliderItemView(returnButton);
            }
        }
    }

    private class ItemButton extends BaseSliderTextButton {
        private Item item;

        public ItemButton(SliderView sliderView, Item item) {
            super(sliderView, item.getName());
            this.item = item;
        }

        @Override // k.p.view.sliderview.BaseSliderTextButton, k.p.view.sliderview.SliderItemView
        public void onClick() {
            String str;
            if (this.item.canUse()) {
                String name = this.item.getName();
                String itemDescription = this.item.getItemDescription();
                if (this.item instanceof Drink) {
                    str = "喝掉";
                } else {
                    str = this.item instanceof BaseEatableItem ? "吃掉" : "使用";
                }
                DialogService.confirm(name, itemDescription, str, "返回", new DialogService.CallBack() { // from class: k.p.view.sliderview.ItemSliderItemList.ItemButton.1
                    @Override // k.p.services.DialogService.CallBack
                    public void onReturn(boolean retVal) {
                        if (retVal) {
                            ItemButton.this.item.use(ItemButton.this.sliderView.mainService.pet, null);
                        }
                    }
                });
            }
        }
    }
}
