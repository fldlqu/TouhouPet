package k.p.view.sliderview;

import java.util.List;
import java.util.Vector;

/* JADX INFO: loaded from: classes.dex */
public class SliderItemList {
    protected float currentPosition;
    protected int itemListHeight;
    protected SliderCanvas sc;
    protected List<SliderItemView> sliderItemList;
    protected SliderView sliderView;
    protected float targetPosition;

    public SliderItemList(SliderView sliderView, SliderCanvas sc) {
        this.sliderView = sliderView;
        this.sc = sc;
        sliderView.allList.add(this);
    }

    public void onHide() {
        for (SliderItemView view : this.sliderItemList) {
            view.onHide();
        }
    }

    public void onShow() {
        for (SliderItemView view : this.sliderItemList) {
            view.onShow();
        }
    }

    public void init() {
        this.sliderItemList = new Vector();
    }

    public void release() {
        for (SliderItemView view : this.sliderItemList) {
            view.release();
        }
    }

    public void addSliderItemView(SliderItemView view) {
        view.setPosition(this.itemListHeight);
        this.sliderItemList.add(view);
        this.itemListHeight += view.getHeight();
    }

    public void clearSliderItemView() {
        this.sliderItemList.clear();
        this.itemListHeight = 0;
    }
}
