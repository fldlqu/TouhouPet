package k.p.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;
import k.p.services.DesktopService;
import local.kcn.utils.MathUtil;
import local.kcn.view.BaseSurfaceView;

/* JADX INFO: loaded from: classes.dex */
public class BaseDesktopView extends BaseSurfaceView {
    private static final float CLICK_DISTANCE_RANGE = 30.0f;
    private static final int CLICK_TIME_RANGE = 500;
    private static final int LONGCLICK_TIME_RANGE = 2000;
    protected int MOVETOTARGET_MINSPEED;
    private Context applicationContext;
    protected Paint clearPaint;
    private long downTime;
    private float downX;
    private float downY;
    private long dragLastTime;
    private float dragLastX;
    private float dragLastY;
    private long dragStartTime;
    private float dragStartX;
    private float dragStartY;
    private Handler handler;
    private boolean isLongClick;
    private LongClickJudgeTask judgeTask;
    protected float moveToTargetBufferedSpeed;
    protected WindowManager.LayoutParams params;
    private Timer timer;
    protected float viewCurrentX;
    protected float viewCurrentY;
    protected int viewHeight;
    protected float viewTargetX;
    protected float viewTargetY;
    protected int viewWidth;

    public BaseDesktopView(Context context) {
        super(context);
        this.moveToTargetBufferedSpeed = 1.0f;
        this.MOVETOTARGET_MINSPEED = 2;
        this.isLongClick = false;
        this.applicationContext = context.getApplicationContext();
    }

    public void toast(String text) {
        toast(text, 0);
    }

    public void toast(String text, int duration) {
        Toast.makeText(this.applicationContext, text, duration).show();
    }

    protected void onClick(float x, float y) {
    }

    protected void onLongClick(float x, float y) {
    }

    protected void onTouch(MotionEvent event) {
    }

    protected void onDown(float x, float y) {
    }

    protected void onMove(float x, float y, int time) {
    }

    protected void onUp(float x, float y) {
    }

    protected void onDrag(float x, float y, int time) {
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [k.p.view.BaseDesktopView$1] */
    public void hide() {
        onHide();
        DesktopService.removeDesktopView(this);
        new Thread() { // from class: k.p.view.BaseDesktopView.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                BaseDesktopView.this.onHideAsync();
            }
        }.start();
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [k.p.view.BaseDesktopView$2] */
    public void show() {
        onShow();
        DesktopService.addDesktopView(this, this.params);
        new Thread() { // from class: k.p.view.BaseDesktopView.2
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                BaseDesktopView.this.onShowAsync();
            }
        }.start();
    }

    protected void onShow() {
    }

    protected void onHide() {
    }

    protected void onShowAsync() {
    }

    protected void onHideAsync() {
    }

    @Override // local.kcn.view.BaseSurfaceView
    protected boolean updateStatus(int time) {
        float distance = Math.abs(this.viewTargetX - this.viewCurrentX);
        if (distance > this.MOVETOTARGET_MINSPEED) {
            this.viewCurrentX = (this.viewTargetX > this.viewCurrentX ? (int) (((distance / 5.0f) * this.moveToTargetBufferedSpeed) + this.MOVETOTARGET_MINSPEED) : (int) (((-distance) / 10.0f) - this.MOVETOTARGET_MINSPEED)) + this.viewCurrentX;
        } else {
            this.viewCurrentX = this.viewTargetX;
        }
        float distance2 = Math.abs(this.viewTargetY - this.viewCurrentY);
        if (distance2 > this.MOVETOTARGET_MINSPEED) {
            this.viewCurrentY = (this.viewTargetY > this.viewCurrentY ? (int) (((distance2 / 5.0f) * this.moveToTargetBufferedSpeed) + this.MOVETOTARGET_MINSPEED) : (int) (((-distance2) / 10.0f) - this.MOVETOTARGET_MINSPEED)) + this.viewCurrentY;
        } else {
            this.viewCurrentY = this.viewTargetY;
        }
        if (this.params.x != ((int) this.viewCurrentX) || this.params.y != ((int) this.viewCurrentY)) {
            this.params.x = (int) this.viewCurrentX;
            this.params.y = (int) this.viewCurrentY;
            this.handler.sendEmptyMessage(0);
            return true;
        }
        return true;
    }

    @Override // local.kcn.view.BaseSurfaceView
    @SuppressLint({"HandlerLeak"})
    protected void init() {
        this.params = new WindowManager.LayoutParams();
        this.params.type = 2003;
        this.params.flags = 776;
        this.params.gravity = 51;
        this.params.x = 0;
        this.params.y = 0;
        this.viewHeight = 50;
        this.viewWidth = 50;
        this.params.width = getViewWidth();
        this.params.height = getViewHeight();
        this.timer = new Timer();
        this.handler = new Handler() { // from class: k.p.view.BaseDesktopView.3
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                DesktopService.refreshDesktopView(BaseDesktopView.this);
            }
        };
        this.clearPaint = new Paint();
        this.clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        long eventTime = event.getEventTime();
        float eventX = event.getRawX();
        float eventY = event.getRawY();
        switch (event.getAction()) {
            case 0:
                this.isLongClick = false;
                this.judgeTask = new LongClickJudgeTask(eventX, eventY);
                this.timer.schedule(this.judgeTask, 2000L);
                this.downTime = eventTime;
                this.downX = eventX;
                this.downY = eventY;
                this.dragLastTime = eventTime;
                this.dragStartTime = eventTime;
                this.dragLastX = eventX;
                this.dragLastY = eventY;
                this.dragStartX = eventX;
                this.dragStartY = eventY;
                onDown(eventX, eventY);
                onTouch(event);
                return true;
            case 1:
                onUp(eventX, eventY);
                this.judgeTask.cancel();
                if (!this.isLongClick) {
                    int touchTime = (int) (eventTime - this.downTime);
                    if (touchTime < CLICK_TIME_RANGE && MathUtil.getDistanceXY(eventX, eventY, this.downX, this.downY) < CLICK_DISTANCE_RANGE) {
                        onClick(eventX, eventY);
                    } else {
                        onDrag(eventX - this.dragStartX, eventY - this.dragStartY, (int) (eventTime - this.dragStartTime));
                    }
                    onTouch(event);
                }
                return true;
            case 2:
                if (!this.isLongClick) {
                    if (MathUtil.getDistanceXY(eventX, eventY, this.dragLastX, this.dragLastY) < 5.0f) {
                        this.dragStartTime = eventTime;
                        this.dragStartX = eventX;
                        this.dragStartY = eventY;
                    }
                    onMove(eventX - this.dragLastX, eventY - this.dragLastY, (int) (eventTime - this.dragLastTime));
                    this.dragLastTime = eventTime;
                    this.dragLastX = eventX;
                    this.dragLastY = eventY;
                    onTouch(event);
                }
                return true;
            default:
                onTouch(event);
                return true;
        }
    }

    private class LongClickJudgeTask extends TimerTask {
        private float x;
        private float y;

        public LongClickJudgeTask(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            if (MathUtil.getDistanceXY(this.x, this.y, BaseDesktopView.this.dragLastX, BaseDesktopView.this.dragLastY) < BaseDesktopView.CLICK_DISTANCE_RANGE) {
                BaseDesktopView.this.isLongClick = true;
                BaseDesktopView.this.onLongClick(this.x, this.y);
            }
        }
    }

    public int getViewWidth() {
        return this.viewWidth;
    }

    public int getViewHeight() {
        return this.viewHeight;
    }

    public float getViewCurrentX() {
        return this.viewCurrentX;
    }

    public float getViewCurrentY() {
        return this.viewCurrentY;
    }

    public float getViewTargetX() {
        return this.viewTargetX;
    }

    public float getViewTargetY() {
        return this.viewTargetY;
    }

    public void setViewTargetX(float x) {
        this.viewTargetX = x;
    }

    public void setViewTargetY(float y) {
        this.viewTargetY = y;
    }

    public void setViewCurrentX(float x) {
        this.viewCurrentX = x;
        this.viewTargetX = x;
    }

    public void setViewCurrentY(float y) {
        this.viewCurrentY = y;
        this.viewTargetY = y;
    }

    public void setViewHeight(int viewHeight) {
        this.viewHeight = viewHeight;
        if (this.params != null) {
            this.params.height = viewHeight;
        }
    }

    public void setViewWidth(int viewWidth) {
        this.viewWidth = viewWidth;
        if (this.params != null) {
            this.params.width = viewWidth;
        }
    }

    public void setMoveToTargetBufferedSpeed(float moveToTargetBufferedSpeed) {
        this.moveToTargetBufferedSpeed = moveToTargetBufferedSpeed;
    }

    public WindowManager.LayoutParams getParams() {
        return this.params;
    }
}
