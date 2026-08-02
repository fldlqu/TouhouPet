package k.p.view.sliderview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Rect;
import local.kcn.utils.LogUtil;

/* JADX INFO: loaded from: classes.dex */
public class BaseSliderButton implements SliderItemView {
    private Bitmap bitmap;
    private int bitmapId;
    private int height = 180;
    private String hint;
    private int position;
    protected SliderView sliderView;
    private Paint textPaint;

    public BaseSliderButton(SliderView sliderView, String hint, int bitmapId) {
        this.sliderView = sliderView;
        this.hint = hint;
        this.bitmapId = bitmapId;
    }

    @Override // k.p.view.sliderview.SliderItemView
    public int getHeight() {
        return (int) (this.height * this.sliderView.getYScale());
    }

    @Override // k.p.view.sliderview.SliderItemView
    public void onDraw(SliderCanvas sc) {
        try {
            if (this.sliderView.textBGBitmap != null && !this.sliderView.textBGBitmap.isRecycled()) {
                sc.drawBitmap(this, this.sliderView.textBGBitmap, null, new Rect(10, 150, 150, 180), null);
            }
            if (this.bitmap != null && !this.bitmap.isRecycled()) {
                sc.drawBitmap(this, this.bitmap, null, new Rect(10, 10, 150, 150), null);
            }
        } catch (Exception e) {
            LogUtil.log(e);
        }
        sc.drawText(this, this.hint, this.sliderView.getViewWidth() / 2, 172, this.textPaint);
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public void setBitmapId(int bitmapId) {
        this.bitmapId = bitmapId;
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
        this.textPaint = new Paint();
        this.textPaint.setColor(-16777216);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.textPaint.setAntiAlias(true);
        this.textPaint.setTextSize(24.0f);
        this.textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        initBitmap();
    }

    private void initBitmap() {
        if (this.bitmap == null) {
            this.bitmap = BitmapFactory.decodeResource(this.sliderView.getResources(), this.bitmapId);
        }
    }

    private void asyncInitBitmap() {
        if (this.bitmap == null) {
            this.bitmap = BitmapFactory.decodeResource(this.sliderView.getResources(), this.bitmapId);
        }
    }

    private void releaseBitmap() {
        if (this.bitmap != null) {
            if (!this.bitmap.isRecycled()) {
                this.bitmap.recycle();
            }
            this.bitmap = null;
        }
    }

    @Override // k.p.view.sliderview.SliderItemView
    public void onShow() {
        asyncInitBitmap();
    }

    @Override // k.p.view.sliderview.SliderItemView
    public void onHide() {
        releaseBitmap();
    }

    @Override // k.p.view.sliderview.SliderItemView
    public void release() {
    }

    @Override // k.p.view.sliderview.SliderItemView
    public void onClick() {
    }
}
