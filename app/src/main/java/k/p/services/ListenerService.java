package k.p.services;

import java.util.ArrayList;
import java.util.List;
import k.p.listener.OnPetPropertyChangeListener;
import k.p.listener.PetPropertyChangeEvent;

/* JADX INFO: loaded from: classes.dex */
public class ListenerService {
    private static List<OnPetPropertyChangeListener> ppcListenerList;

    public static void init() {
        ppcListenerList = new ArrayList();
    }

    public static void registerListener(OnPetPropertyChangeListener listener) {
        ppcListenerList.add(listener);
    }

    public static void notifyListener(PetPropertyChangeEvent event) {
        for (OnPetPropertyChangeListener listener : ppcListenerList) {
            listener.onPetPropertyChange(event);
        }
    }

    public static void release() {
        ppcListenerList = null;
    }
}
