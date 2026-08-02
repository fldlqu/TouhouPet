package k.p.view.sliderview;

/* JADX INFO: loaded from: classes.dex */
public interface SliderItemView {
    int getHeight();

    int getPosition();

    void init();

    void onClick();

    void onDraw(SliderCanvas sliderCanvas);

    void onHide();

    void onShow();

    void release();

    void setPosition(int i);
}
