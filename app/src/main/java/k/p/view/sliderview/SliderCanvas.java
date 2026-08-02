package k.p.view.sliderview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/* JADX INFO: loaded from: classes.dex */
public interface SliderCanvas {
    void drawBitmap(SliderItemView sliderItemView, Bitmap bitmap, Rect rect, Rect rect2, Paint paint);

    void drawText(SliderItemView sliderItemView, String str, int i, int i2, Paint paint);

    void setCanvas(Canvas canvas);
}
