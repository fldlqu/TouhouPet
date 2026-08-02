package k.p.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import java.util.HashMap;
import java.util.Map;
import k.p.animation.PetAnimation;
import k.p.animation.PetAnimationInfo;
import k.p.domain.BasePet;
import k.p.domain.states.SleepState;
import k.p.main.MainService;
import k.p.services.AnimationService;
import k.p.services.DialogService;
import k.p.services.ViewService;
import k.p.utils.EnvironmentUtil;

/* JADX INFO: loaded from: classes.dex */
public class PetView extends BaseDesktopView {
    private static final int MIN_CLICK_INTERVAL = 500;
    private Map<String, Bitmap> bitmapMap;
    private PetAnimation currentAnimation;
    private PetAnimationInfo currentAnimationInfo;
    private Bitmap currentBitmap;
    private Bitmap lastBitmap;
    private int lastClickTime;
    private BasePet pet;
    private Rect rect;
    private boolean showControl;
    private int totalTime;

    public PetView(Context context) {
        super(context);
        this.lastBitmap = null;
        this.currentBitmap = null;
        this.showControl = false;
        this.pet = ((MainService) context).pet;
    }

    @Override // k.p.view.BaseDesktopView, local.kcn.view.BaseSurfaceView
    protected void init() {
        super.init();
        if (AnimationService.petHeight > 0 && AnimationService.petWidth > 0) {
            setViewWidth(AnimationService.petWidth);
            setViewHeight(AnimationService.petHeight);
        } else {
            setViewWidth(128);
            setViewHeight(128);
        }
        setViewCurrentX(this.SCREEN_WIDTH / 2);
        setViewCurrentY(this.SCREEN_HEIGHT / 2);
        this.params.x = this.SCREEN_WIDTH / 2;
        this.params.y = this.SCREEN_HEIGHT / 2;
        setCurrentFPS(40.0f);
        this.rect = new Rect(0, 0, this.viewWidth, this.viewHeight);
        this.bitmapMap = new HashMap();
    }

    @Override // local.kcn.view.BaseSurfaceView
    protected void asyncInit() {
        try {
            Thread.sleep(50L);
        } catch (InterruptedException e) {
        }
        newRandomAnimation();
    }

    private Bitmap getBitmap(String picPath) {
        Bitmap bitmap = this.bitmapMap.get(picPath);
        if (bitmap == null) {
            Bitmap bitmap2 = loadBitmap(String.valueOf(EnvironmentUtil.getMainPath()) + "/pet/animations" + picPath);
            this.bitmapMap.put(picPath, bitmap2);
            return bitmap2;
        }
        return bitmap;
    }

    @Override // k.p.view.BaseDesktopView, local.kcn.view.BaseSurfaceView
    protected boolean updateStatus(int time) {
        super.updateStatus(time);
        this.lastClickTime -= time;
        this.totalTime += time;
        if (this.totalTime > this.currentAnimationInfo.getDelay()) {
            this.currentAnimationInfo = this.currentAnimation.nextFrame();
            if (this.currentAnimationInfo == null) {
                newRandomAnimation();
            }
            this.totalTime = 0;
        }
        this.lastBitmap = this.currentBitmap;
        this.currentBitmap = getBitmap(this.currentAnimationInfo.getPicPath());
        return this.lastBitmap != this.currentBitmap;
    }

    private void newRandomAnimation() {
        if (this.pet.getCurrentState() instanceof SleepState) {
            AnimationService.requestChangeAnimation(AnimationService.getRandomAnimationByType("SLEEP"));
        } else {
            AnimationService.requestChangeAnimation(AnimationService.getRandomAnimationByType("ACTIVE"));
        }
    }

    public void changeAnimation(PetAnimation animation) {
        this.currentAnimation = animation;
        this.currentAnimationInfo = animation.nextFrame();
    }

    @Override // k.p.view.BaseDesktopView
    protected void onMove(float x, float y, int time) {
        setViewCurrentX((int) (getViewCurrentX() + x));
        setViewCurrentY((int) (getViewCurrentY() + y));
    }

    @Override // local.kcn.view.BaseSurfaceView
    protected void update(Canvas canvas) {
        canvas.drawPaint(this.clearPaint);
        if (this.currentBitmap != null) {
            canvas.drawBitmap(this.currentBitmap, (Rect) null, this.rect, this.picPaint);
        }
    }

    @Override // k.p.view.BaseDesktopView
    protected void onHide() {
        if (this.showControl) {
            ViewService.sliderHandlerView.hide();
            ViewService.sliderView.hide();
            this.showControl = false;
        }
        if (DialogService.currentDialogView != null) {
            DialogService.clearDialog();
        }
    }

    @Override // k.p.view.BaseDesktopView
    protected void onLongClick(float x, float y) {
        ((MainService) this.context).exit();
    }

    @Override // k.p.view.BaseDesktopView
    protected void onClick(float x, float y) {
        if (DialogService.currentDialogView == null && this.lastClickTime <= 0) {
            this.lastClickTime = MIN_CLICK_INTERVAL;
            if (this.showControl) {
                ViewService.sliderHandlerView.hide();
                ViewService.sliderView.hide();
                this.showControl = false;
            } else {
                ViewService.sliderHandlerView.show();
                ViewService.sliderView.show();
                this.showControl = true;
            }
        }
    }
}
