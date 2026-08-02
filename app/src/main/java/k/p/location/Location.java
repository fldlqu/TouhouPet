package k.p.location;

import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface Location {
    String getDescription();

    String getName();

    List<Location> getNearbyList();

    void init();

    boolean isVisible();

    void onEnter();

    void onFinishSearch();

    void onLeave();

    void onStartSearch();

    void setVisible(boolean z);
}
