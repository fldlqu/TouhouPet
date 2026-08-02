package k.p.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import k.p.domain.BasePet;
import k.p.main.MainService;
import k.p.main.R;

/* JADX INFO: loaded from: classes.dex */
public class StatusView extends BaseDesktopView {
    private static float PROGRESSBAR_HEIGHT;
    private static float PROGRESSBAR_WIDTH;
    private static float PROGRESSBAR_X_OFFSET;
    private static float PROGRESSBAR_Y_OFFSET;
    private static float TEXT_X_OFFSET;
    private static float TEXT_Y_INTERVAL;
    private static float TEXT_Y_OFFSET;
    private Bitmap bgBitmap;
    private Paint bgPaint;
    private Rect drawRect;
    private int fadeTime;
    private BasePet pet;
    private Bitmap progressBGBitmap;
    private Bitmap progressRateBitmap;
    public int stage;
    private Paint textPaint;
    protected float viewAlpha;

    public StatusView(Context context) {
        super(context);
        this.stage = 1;
        this.pet = ((MainService) context).pet;
    }

    @Override // k.p.view.BaseDesktopView, local.kcn.view.BaseSurfaceView
    @SuppressLint({"HandlerLeak"})
    protected void init() {
        super.init();
        setViewWidth((int) (this.X_SCALE * 300.0f));
        setViewHeight((int) (this.Y_SCALE * 500.0f));
        setCurrentFPS(20.0f);
        setViewCurrentX(this.X_SCALE * 180.0f);
        this.params.x = (int) (this.X_SCALE * 180.0f);
        this.bgBitmap = loadBitmap(R.drawable.statusview_bg);
        this.progressBGBitmap = loadBitmap(R.drawable.progress_bg);
        this.progressRateBitmap = loadBitmap(R.drawable.progress_rate);
        this.drawRect = new Rect(0, 0, (int) (this.X_SCALE * 300.0f), (int) (this.Y_SCALE * 500.0f));
        this.bgPaint = new Paint();
        this.textPaint = new Paint();
        this.textPaint.setColor(-16777216);
        this.textPaint.setTextAlign(Paint.Align.LEFT);
        this.textPaint.setAntiAlias(true);
        this.textPaint.setTextSize(24.0f * this.X_SCALE);
        this.textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        TEXT_X_OFFSET = this.X_SCALE * 20.0f;
        TEXT_Y_OFFSET = 40.0f * this.Y_SCALE;
        TEXT_Y_INTERVAL = 30.0f * this.Y_SCALE;
        PROGRESSBAR_WIDTH = 162.0f * this.X_SCALE;
        PROGRESSBAR_HEIGHT = 10.0f * this.Y_SCALE;
        PROGRESSBAR_X_OFFSET = 70.0f * this.X_SCALE;
        PROGRESSBAR_Y_OFFSET = (-13.0f) * this.Y_SCALE;
    }

    @Override // k.p.view.BaseDesktopView
    public void show() {
        setViewCurrentX(this.X_SCALE * 180.0f);
        this.params.x = (int) (this.X_SCALE * 180.0f);
        setViewCurrentY(0.0f);
        this.params.y = 0;
        this.fadeTime = 1000;
        this.stage = 2;
        super.show();
    }

    @Override // k.p.view.BaseDesktopView
    public void hide() {
        this.fadeTime = 1000;
        this.stage = 3;
    }

    public void hideImmediately() {
        if (this.stage != 1) {
            super.hide();
            this.fadeTime = 0;
            this.stage = 1;
            setViewCurrentX(0.0f);
            this.params.x = 0;
            setViewAlpha(0.0f);
        }
    }

    @Override // k.p.view.BaseDesktopView, local.kcn.view.BaseSurfaceView
    protected boolean updateStatus(int time) {
        this.fadeTime -= time;
        if (this.fadeTime < 0) {
            this.fadeTime = 0;
        }
        if (this.stage == 2) {
            if (this.fadeTime <= 0) {
                this.stage = 0;
            }
            setViewAlpha((1000 - this.fadeTime) / 1000.0f);
        } else if (this.stage == 3) {
            if (this.fadeTime <= 0) {
                this.stage = 1;
                super.hide();
            }
            setViewAlpha(this.fadeTime / 1000.0f);
        }
        super.updateStatus(time);
        return true;
    }

    @Override // local.kcn.view.BaseSurfaceView
    protected void update(Canvas canvas) {
        this.bgPaint.setAlpha((int) (this.viewAlpha * 255.0f));
        this.textPaint.setAlpha((int) (this.viewAlpha * 255.0f));
        canvas.drawBitmap(this.bgBitmap, (Rect) null, this.drawRect, this.clearPaint);
        canvas.drawBitmap(this.bgBitmap, (Rect) null, this.drawRect, this.bgPaint);
        this.textPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(this.pet.getName(), TEXT_X_OFFSET, TEXT_Y_OFFSET + (0.0f * TEXT_Y_INTERVAL), this.textPaint);
        canvas.drawText("等级 : " + this.pet.getLevel(), TEXT_X_OFFSET, TEXT_Y_OFFSET + (1.0f * TEXT_Y_INTERVAL), this.textPaint);
        canvas.drawText("状态 : " + this.pet.getCurrentState().getStateDoingDescription(), TEXT_X_OFFSET, TEXT_Y_OFFSET + (2.0f * TEXT_Y_INTERVAL), this.textPaint);
        canvas.drawText("成就: " + this.pet.getAchievement(), TEXT_X_OFFSET, TEXT_Y_OFFSET + (3.0f * TEXT_Y_INTERVAL), this.textPaint);
        canvas.drawText("成长: " + this.pet.getLifeTimeByChinese(), TEXT_X_OFFSET, TEXT_Y_OFFSET + (4.0f * TEXT_Y_INTERVAL), this.textPaint);
        canvas.drawText("点数 : " + this.pet.getPoint(), TEXT_X_OFFSET, TEXT_Y_OFFSET + (10.0f * TEXT_Y_INTERVAL), this.textPaint);
        canvas.drawText("P 点 : " + this.pet.getPower(), TEXT_X_OFFSET, TEXT_Y_OFFSET + (11.0f * TEXT_Y_INTERVAL), this.textPaint);
        canvas.drawText("力量 : " + this.pet.getStrength(), TEXT_X_OFFSET, TEXT_Y_OFFSET + (12.0f * TEXT_Y_INTERVAL), this.textPaint);
        canvas.drawText("速度 : " + this.pet.getSpeed(), TEXT_X_OFFSET, TEXT_Y_OFFSET + (13.0f * TEXT_Y_INTERVAL), this.textPaint);
        canvas.drawText("灵力 : " + this.pet.getMagic(), TEXT_X_OFFSET, TEXT_Y_OFFSET + (14.0f * TEXT_Y_INTERVAL), this.textPaint);
        drawProgress(canvas, "经验 :", 5, this.pet.getCurrentExp(), this.pet.getNextLevelExp());
        drawProgress(canvas, "饱食 :", 6, this.pet.getRepletionDegree(), 100);
        drawProgress(canvas, "饮水 :", 7, this.pet.getDrinkDegree(), 100);
        drawProgress(canvas, "精力 :", 8, this.pet.getEnergy(), 100);
    }

    private void drawProgress(Canvas canvas, String title, int index, int currentValue, int maxValue) {
        this.textPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(title, TEXT_X_OFFSET, TEXT_Y_OFFSET + (index * TEXT_Y_INTERVAL), this.textPaint);
        float rate = currentValue / maxValue;
        canvas.drawBitmap(this.progressBGBitmap, (Rect) null, new Rect((int) (TEXT_X_OFFSET + PROGRESSBAR_X_OFFSET), (int) (TEXT_Y_OFFSET + PROGRESSBAR_Y_OFFSET + (index * TEXT_Y_INTERVAL)), (int) (TEXT_X_OFFSET + PROGRESSBAR_X_OFFSET + PROGRESSBAR_WIDTH), (int) (TEXT_Y_OFFSET + PROGRESSBAR_Y_OFFSET + (index * TEXT_Y_INTERVAL) + PROGRESSBAR_HEIGHT)), this.bgPaint);
        canvas.drawBitmap(this.progressRateBitmap, (Rect) null, new Rect((int) (TEXT_X_OFFSET + PROGRESSBAR_X_OFFSET), (int) (TEXT_Y_OFFSET + PROGRESSBAR_Y_OFFSET + (index * TEXT_Y_INTERVAL)), (int) (TEXT_X_OFFSET + PROGRESSBAR_X_OFFSET + (PROGRESSBAR_WIDTH * rate)), (int) (TEXT_Y_OFFSET + PROGRESSBAR_Y_OFFSET + (index * TEXT_Y_INTERVAL) + PROGRESSBAR_HEIGHT)), this.bgPaint);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(String.valueOf(currentValue) + "/" + maxValue, TEXT_X_OFFSET + PROGRESSBAR_X_OFFSET + (PROGRESSBAR_WIDTH / 2.0f), TEXT_Y_OFFSET + (index * TEXT_Y_INTERVAL), this.textPaint);
    }

    public float getViewAlpha() {
        return this.viewAlpha;
    }

    public void setViewAlpha(float viewAlpha) {
        this.viewAlpha = viewAlpha;
        if (this.viewAlpha > 1.0f) {
            this.viewAlpha = 1.0f;
        } else if (this.viewAlpha < 0.0f) {
            this.viewAlpha = 0.0f;
        }
    }

    @Override // k.p.view.BaseDesktopView
    protected void onMove(float x, float y, int time) {
        setViewCurrentX(getViewCurrentX() + x);
        setViewCurrentY(getViewCurrentY() + y);
    }
}
