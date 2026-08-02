package k.p.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import k.p.main.R;
import k.p.services.ViewService;
import k.p.view.sliderview.SliderView;

/* JADX INFO: loaded from: classes.dex */
public class SliderHandlerView extends BaseDesktopView {
    private Bitmap bitmap;
    private Rect drawRect;
    private boolean drawed;
    private SliderView sv;

    public SliderHandlerView(Context context) {
        super(context);
        this.drawed = false;
    }

    @Override // k.p.view.BaseDesktopView, local.kcn.view.BaseSurfaceView
    @SuppressLint({"HandlerLeak"})
    protected void init() {
        super.init();
        setCurrentFPS(30.0f);
        setViewHeight((int) (200.0f * this.Y_SCALE));
        setViewWidth((int) (100.0f * this.X_SCALE));
        setViewCurrentX(0.0f);
        setViewCurrentY(this.Y_SCALE * 800.0f);
        this.params.y = (int) (this.Y_SCALE * 800.0f);
        this.bitmap = loadBitmap(R.drawable.slider_handler);
        this.drawRect = new Rect(0, 0, this.viewWidth, this.viewHeight);
        this.sv = ViewService.sliderView;
    }

    @Override // k.p.view.BaseDesktopView, local.kcn.view.BaseSurfaceView
    protected boolean updateStatus(int time) {
        super.updateStatus(time);
        return !this.drawed;
    }

    @Override // local.kcn.view.BaseSurfaceView
    public void requestPause() {
        super.requestPause();
        this.drawed = false;
    }

    @Override // local.kcn.view.BaseSurfaceView
    protected void update(Canvas canvas) {
        if (this.bitmap != null) {
            canvas.drawBitmap(this.bitmap, (Rect) null, this.drawRect, this.picPaint);
            this.drawed = true;
        }
    }

    @Override // k.p.view.BaseDesktopView
    protected void onMove(float x, float y, int time) {
        int targetX = (int) (getViewCurrentX() + x);
        if (targetX > this.sv.viewWidth) {
            targetX = this.sv.viewWidth;
        } else if (targetX < 0) {
            targetX = 0;
        }
        setViewCurrentX(targetX);
        this.sv.setViewCurrentX(targetX - this.sv.viewWidth);
    }

    @Override // k.p.view.BaseDesktopView
    protected void onUp(float x, float y) {
        if (getViewCurrentX() < this.sv.viewWidth / 2) {
            setViewTargetX(0.0f);
            this.sv.setViewTargetX(-this.sv.viewWidth);
        } else {
            setViewTargetX(this.sv.viewWidth);
            this.sv.setViewTargetX(0.0f);
        }
    }

    @Override // k.p.view.BaseDesktopView
    protected void onHide() {
        setViewCurrentX(0.0f);
        this.params.x = 0;
    }
}
