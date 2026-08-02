package k.p.listener;

import k.p.item.Item;

/* JADX INFO: loaded from: classes.dex */
public class ItemChangeEvent {
    public static final int DROP = 1;
    public static final int GET = 0;
    public static final int USE = 2;
    private Item item;
    private int type;

    public ItemChangeEvent(Item item, int type) {
        this.item = item;
        this.type = type;
    }

    public Item getItem() {
        return this.item;
    }

    public int getType() {
        return this.type;
    }
}
