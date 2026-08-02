package k.p.view.sliderview;

import android.graphics.Paint;
import android.graphics.Rect;

/* JADX INFO: loaded from: classes.dex */
public class BaseSliderTitleTextButton implements SliderItemView {
    private String hint;
    private int position;
    protected SliderView sliderView;
    private int height = 70;
    private Paint textPaint = new Paint();

    public BaseSliderTitleTextButton(SliderView sliderView, String hint) {
        this.sliderView = sliderView;
        this.hint = hint;
        this.textPaint.setColor(-16777216);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.textPaint.setAntiAlias(true);
        this.textPaint.setTextSize(36.0f);
        this.textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override // k.p.view.sliderview.SliderItemView
    public int getHeight() {
        return (int) (this.height * this.sliderView.getYScale());
    }

    @Override // k.p.view.sliderview.SliderItemView
    public void onDraw(SliderCanvas sc) {
        if (this.sliderView.titleTextBGBitmap != null && !this.sliderView.titleTextBGBitmap.isRecycled()) {
            sc.drawBitmap(this, this.sliderView.titleTextBGBitmap, null, new Rect(1, 10, 159, 60), null);
        }
        sc.drawText(this, this.hint, this.sliderView.getViewWidth() / 2, 45, this.textPaint);
    }

    @Override // k.p.view.sliderview.SliderItemView
    public void setPosition(int position) {
        this.position = position;
    }

    @Override // k.p.view.sliderview.SliderItemView
    public int getPosition() {
        return this.position;
    }

    @Override // k.p.view.sliderview.SliderItemView
    public void init() {
    }

    @Override // k.p.view.sliderview.SliderItemView
    public void release() {
    }

    @Override // k.p.view.sliderview.SliderItemView
    public void onClick() {
    }

    @Override // k.p.view.sliderview.SliderItemView
    public void onShow() {
    }

    @Override // k.p.view.sliderview.SliderItemView
    public void onHide() {
    }
}
