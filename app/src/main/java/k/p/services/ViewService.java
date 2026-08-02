package k.p.services;

import k.p.view.PetView;
import k.p.view.SliderHandlerView;
import k.p.view.StatusView;
import k.p.view.sliderview.SliderView;

/* JADX INFO: loaded from: classes.dex */
public class ViewService {
    public static PetView petView;
    public static SliderHandlerView sliderHandlerView;
    public static SliderView sliderView;
    public static StatusView statusView;

    public static void release() {
        petView = null;
        sliderView = null;
        sliderHandlerView = null;
        statusView = null;
    }
}
