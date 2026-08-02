package k.p.item;

import k.p.domain.BasePet;
import k.p.domain.ReturnStatus;

/* JADX INFO: loaded from: classes.dex */
public interface Item {
    public static final int DROP = 0;
    public static final int SOLD = 1;
    public static final int USE = 3;

    boolean canUse();

    String getItemDescription();

    String getItemType();

    String getName();

    void onDestroy(int i);

    void onGet();

    ReturnStatus<?> use(BasePet basePet, Object obj);
}
