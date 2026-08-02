package k.p.view.sliderview;

import k.p.main.R;

/* JADX INFO: loaded from: classes.dex */
public class ReturnButton extends BaseSliderButton {
    public ReturnButton(SliderView sliderView) {
        super(sliderView, "返回", R.drawable.button_return);
    }

    @Override // k.p.view.sliderview.BaseSliderButton, k.p.view.sliderview.SliderItemView
    public void onClick() {
        this.sliderView.currentSliderItemList = this.sliderView.mainList;
    }
}
