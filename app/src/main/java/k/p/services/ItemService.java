package k.p.services;

import java.util.ArrayList;
import java.util.List;
import k.p.item.BaseItem;
import k.p.listener.ItemChangeEvent;
import k.p.listener.OnItemChangeListener;

/* JADX INFO: loaded from: classes.dex */
public class ItemService {
    private static List<OnItemChangeListener> listenerList;

    public static void init() {
        listenerList = new ArrayList();
    }

    public static List<BaseItem> getAllItems() {
        return PetService.pet.getAllItems();
    }

    public static List<BaseItem> getItemsByType(String itemType) {
        List<BaseItem> list = new ArrayList<>();
        for (BaseItem item : PetService.pet.getAllItems()) {
            if (itemType.equals(item.getItemType())) {
                list.add(item);
            }
        }
        return list;
    }

    public static List<BaseItem> getItemsByName(String itemName) {
        List<BaseItem> list = new ArrayList<>();
        for (BaseItem item : PetService.pet.getAllItems()) {
            if (itemName.equals(item.getName())) {
                list.add(item);
            }
        }
        return list;
    }

    public static void addItem(BaseItem item) {
        item.onGet();
        PetService.pet.getAllItems().add(item);
        if (listenerList.size() > 0) {
            ItemChangeEvent event = new ItemChangeEvent(item, 0);
            for (OnItemChangeListener listener : listenerList) {
                listener.onItemChange(event);
            }
        }
    }

    public static void useItem(BaseItem item) {
        PetService.pet.getAllItems().remove(item);
        if (listenerList.size() > 0) {
            ItemChangeEvent event = new ItemChangeEvent(item, 2);
            for (OnItemChangeListener listener : listenerList) {
                listener.onItemChange(event);
            }
        }
    }

    public static void dropItem(BaseItem item) {
        PetService.pet.getAllItems().remove(item);
        if (listenerList.size() > 0) {
            ItemChangeEvent event = new ItemChangeEvent(item, 1);
            for (OnItemChangeListener listener : listenerList) {
                listener.onItemChange(event);
            }
        }
    }

    public static void registerItemListener(OnItemChangeListener listener) {
        listenerList.add(listener);
    }

    public static void release() {
        listenerList = null;
    }
}
