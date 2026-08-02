package k.p.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import java.io.File;
import k.p.utils.EnvironmentUtil;
import local.kcn.utils.LogUtil;
import local.kcn.view.BaseSurfaceView;

/* JADX INFO: loaded from: classes.dex */
public class MainView extends BaseSurfaceView {
    private float bgAlpha;
    private Bitmap bgBitmap;
    private float blinkAlpha;
    private float boardBlinkAlpha;
    private Bitmap boardBlinkBitmap;
    private float boardBlinkDelay;
    private Bitmap buttonBitmap;
    private float buttonExitX;
    private float buttonExitY;
    private float buttonHeight;
    private float buttonStartX;
    private float buttonStartY;
    private float buttonWidth;
    private TouhouPet context;
    private MediaPlayer mp;
    private float musicBPM;
    private int musicOffset;
    private boolean playing;
    private float satoriBlinkAngel;
    private Bitmap satoriBlinkBitmap;
    private int satoriBlinkHeight;
    private int satoriBlinkWidth;
    private int satoriBlinkX;
    private int satoriBlinkY;
    private int stage;
    private int startBoardBlinkOffset;
    private Paint textPaint;
    private float titlePetAlpha;
    private Bitmap titlePetBitmap;
    private float titlePetHeight;
    private float titlePetWidth;
    private float titlePetX;
    private float titlePetY;
    private float titleSatoriAlpha;
    private Bitmap titleSatoriBitmap;
    private float titleSatoriHeight;
    private float titleSatoriWidth;
    private float titleSatoriX;
    private float titleSatoriY;
    private float titleTargetX;
    private float titleTouhouAlpha;
    private Bitmap titleTouhouBitmap;
    private float titleTouhouHeight;
    private float titleTouhouWidth;
    private float titleTouhouX;
    private float titleTouhouY;

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.stage = 0;
        this.musicBPM = 151.0f;
        this.startBoardBlinkOffset = 14877;
        this.boardBlinkDelay = 400.0f;
        this.playing = false;
        this.context = (TouhouPet) context;
    }

    @Override // local.kcn.view.BaseSurfaceView
    protected void init() {
        this.bgBitmap = loadBitmap(R.drawable.bg);
        this.titleTouhouBitmap = loadBitmap(R.drawable.title_touhou);
        this.titlePetBitmap = loadBitmap(R.drawable.title_pet);
        this.titleSatoriBitmap = loadBitmap(R.drawable.title_satori);
        this.buttonBitmap = loadBitmap(R.drawable.title_text_bg);
        this.boardBlinkBitmap = loadBitmap(R.drawable.boardblink_bg);
        this.satoriBlinkBitmap = loadBitmap(R.drawable.satori_blink);
        this.mp = new MediaPlayer();
        try {
            File file = new File(String.valueOf(EnvironmentUtil.getMainPath()) + "/system/Rouzensatorin.mp3");
            LogUtil.log("path = " + file.getAbsolutePath());
            LogUtil.log("exist = " + file.exists());
            this.mp.setDataSource(String.valueOf(EnvironmentUtil.getMainPath()) + "/system/Rouzensatorin.mp3");
            this.mp.prepare();
            this.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: k.p.main.MainView.1
                @Override // android.media.MediaPlayer.OnCompletionListener
                public void onCompletion(MediaPlayer mp) {
                    MainView.this.stage = 7;
                }
            });
        } catch (Exception e) {
            LogUtil.log(e);
        }
        this.textPaint = new Paint();
        this.textPaint.setColor(-16777216);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.textPaint.setAntiAlias(true);
        this.textPaint.setTextSize(34.0f);
        this.textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.titleTargetX = this.SCREEN_WIDTH / 2;
        this.titleTouhouWidth = 600.0f * this.X_SCALE;
        this.titleTouhouHeight = this.Y_SCALE * 200.0f;
        this.titlePetWidth = this.X_SCALE * 400.0f;
        this.titlePetHeight = this.Y_SCALE * 200.0f;
        this.titleSatoriWidth = 300.0f * this.X_SCALE;
        this.titleSatoriHeight = 100.0f * this.Y_SCALE;
        this.buttonWidth = 240.0f * this.X_SCALE;
        this.buttonHeight = 60.0f * this.Y_SCALE;
        this.satoriBlinkWidth = (int) (this.X_SCALE * 128.0f);
        this.satoriBlinkHeight = (int) (this.Y_SCALE * 128.0f);
        this.titleTouhouX = (-this.titleTouhouWidth) / 2.0f;
        this.titleTouhouY = 150.0f * this.Y_SCALE;
        this.titlePetX = this.SCREEN_WIDTH + (this.titleTouhouWidth / 2.0f);
        this.titlePetY = 350.0f * this.Y_SCALE;
        this.buttonStartX = 0.0f;
        this.buttonStartY = 800.0f * this.Y_SCALE;
        this.buttonExitX = this.SCREEN_WIDTH;
        this.buttonExitY = 950.0f * this.Y_SCALE;
        this.titleSatoriX = (this.SCREEN_WIDTH / 2) + (this.titleSatoriWidth / 2.0f) + (50.0f * this.X_SCALE);
        this.titleSatoriY = 500.0f * this.Y_SCALE;
        this.satoriBlinkX = (int) (this.X_SCALE * 400.0f);
        this.satoriBlinkY = (int) (480.0f * this.Y_SCALE);
        startDraw();
    }

    @Override // local.kcn.view.BaseSurfaceView
    protected boolean updateStatus(int time) {
        if (this.stage == 0 && this.offset >= 2000) {
            this.stage = 1;
        }
        if (this.stage == 1 && this.offset >= 5000) {
            this.stage = 2;
        }
        if (this.stage == 2 && this.offset >= 7000) {
            this.stage = 3;
        }
        if (this.stage == 3 && this.offset >= 8000) {
            this.stage = 4;
        }
        if (this.stage == 4 && this.offset >= 20000) {
            this.stage = 5;
        }
        if (this.stage == 5 && this.offset >= 22000) {
            this.stage = 6;
        }
        if (!this.playing && this.offset > 6427) {
            LogUtil.log("music start");
            this.mp.start();
            this.offset = 6427L;
            this.playing = true;
        }
        this.musicOffset = (int) (this.offset - 6427);
        if (this.musicOffset > this.startBoardBlinkOffset) {
            int cof = this.musicOffset - this.startBoardBlinkOffset;
            int interval = (int) (60000.0f / this.musicBPM);
            int f = cof % interval;
            if (f < this.boardBlinkDelay) {
                this.boardBlinkAlpha = (this.boardBlinkDelay - f) / this.boardBlinkDelay;
                this.boardBlinkAlpha /= 1.8f;
            } else {
                this.boardBlinkAlpha = 0.0f;
            }
        }
        this.satoriBlinkAngel += time / 10.0f;
        if (this.stage == 1) {
            if (time < 10) {
                time = 10;
            }
            float rate = 500.0f / time;
            float distance = Math.abs(this.titleTouhouX - this.titleTargetX);
            if (distance > 5.0f) {
                this.titleTouhouX = (this.titleTouhouX - this.titleTargetX > 0.0f ? ((-distance) / rate) - 1.0f : (distance / rate) + 1.0f) + this.titleTouhouX;
            } else {
                this.titleTouhouX = this.titleTargetX;
            }
            this.titleTouhouAlpha = ((this.titleTargetX - distance) - 100.0f) / this.titleTargetX;
            float distance2 = Math.abs(this.titlePetX - this.titleTargetX);
            if (distance2 > 5.0f) {
                this.titlePetX = (this.titlePetX - this.titleTargetX > 0.0f ? ((-distance2) / rate) - 1.0f : (distance2 / rate) + 1.0f) + this.titlePetX;
            } else {
                this.titlePetX = this.titleTargetX;
            }
            this.titlePetAlpha = ((this.titleTargetX - distance2) - 100.0f) / this.titleTargetX;
        } else if (this.stage == 2) {
            this.titleSatoriAlpha += time / 2000.0f;
            if (this.titleSatoriAlpha > 1.0f) {
                this.titleSatoriAlpha = 1.0f;
            }
        } else if (this.stage == 3) {
            this.blinkAlpha = ((8000 - this.offset) / 1000.0f) / 1.4f;
        } else if (this.stage == 4) {
            this.bgAlpha += time / 15000.0f;
            if (this.bgAlpha > 1.0f) {
                this.bgAlpha = 1.0f;
            }
        } else if (this.stage == 5) {
            if (time < 10) {
                time = 10;
            }
            float rate2 = 300.0f / time;
            float distance3 = Math.abs(this.buttonStartX - this.titleTargetX);
            if (distance3 > 5.0f) {
                this.buttonStartX = (this.buttonStartX - this.titleTargetX > 0.0f ? ((-distance3) / rate2) - 1.0f : (distance3 / rate2) + 1.0f) + this.buttonStartX;
            } else {
                this.buttonStartX = this.titleTargetX;
            }
            float distance4 = Math.abs(this.buttonExitX - this.titleTargetX);
            if (distance4 > 5.0f) {
                this.buttonExitX = (this.buttonExitX - this.titleTargetX > 0.0f ? ((-distance4) / rate2) - 1.0f : (distance4 / rate2) + 1.0f) + this.buttonExitX;
            } else {
                this.buttonExitX = this.titleTargetX;
            }
        }
        return true;
    }

    @Override // local.kcn.view.BaseSurfaceView
    protected void update(Canvas canvas) {
        float f;
        canvas.drawColor(-16777216);
        if (this.stage >= 3) {
            this.picPaint.setAlpha((int) (255.0f * (this.bgAlpha / 2.0f)));
            canvas.drawBitmap(this.bgBitmap, (Rect) null, new Rect(0, 0, this.SCREEN_WIDTH, this.SCREEN_HEIGHT), this.picPaint);
        }
        this.picPaint.setAlpha((int) (255.0f * this.titleTouhouAlpha));
        canvas.drawBitmap(this.titleTouhouBitmap, (Rect) null, new Rect((int) (this.titleTouhouX - (this.titleTouhouWidth / 2.0f)), (int) (this.titleTouhouY - (this.titleTouhouHeight / 2.0f)), (int) (this.titleTouhouX + (this.titleTouhouWidth / 2.0f)), (int) (this.titleTouhouY + (this.titleTouhouHeight / 2.0f))), this.picPaint);
        this.picPaint.setAlpha((int) (255.0f * this.titlePetAlpha));
        canvas.drawBitmap(this.titlePetBitmap, (Rect) null, new Rect((int) (this.titlePetX - (this.titlePetWidth / 2.0f)), (int) (this.titlePetY - (this.titlePetHeight / 2.0f)), (int) (this.titlePetX + (this.titlePetWidth / 2.0f)), (int) (this.titlePetY + (this.titlePetHeight / 2.0f))), this.picPaint);
        this.picPaint.setAlpha((int) (255.0f * this.titleSatoriAlpha));
        canvas.drawBitmap(this.titleSatoriBitmap, (Rect) null, new Rect((int) (this.titleSatoriX - (this.titleSatoriWidth / 2.0f)), (int) (this.titleSatoriY - (this.titleSatoriHeight / 2.0f)), (int) (this.titleSatoriX + (this.titleSatoriWidth / 2.0f)), (int) (this.titleSatoriY + (this.titleSatoriHeight / 2.0f))), this.picPaint);
        if (this.stage >= 5) {
            this.picPaint.setAlpha(255);
            canvas.drawBitmap(this.buttonBitmap, (Rect) null, new Rect((int) (this.buttonStartX - (this.buttonWidth / 2.0f)), (int) (this.buttonStartY - (this.buttonHeight / 2.0f)), (int) (this.buttonStartX + (this.buttonWidth / 2.0f)), (int) (this.buttonStartY + (this.buttonHeight / 2.0f))), this.picPaint);
            canvas.drawBitmap(this.buttonBitmap, (Rect) null, new Rect((int) (this.buttonExitX - (this.buttonWidth / 2.0f)), (int) (this.buttonExitY - (this.buttonHeight / 2.0f)), (int) (this.buttonExitX + (this.buttonWidth / 2.0f)), (int) (this.buttonExitY + (this.buttonHeight / 2.0f))), this.picPaint);
            canvas.drawText("开始", this.buttonStartX, this.buttonStartY + (this.buttonHeight / 4.0f), this.textPaint);
            canvas.drawText("退出", this.buttonExitX, this.buttonExitY + (this.buttonHeight / 4.0f), this.textPaint);
        }
        if (this.stage == 3 || this.stage == 4) {
            canvas.drawColor(Color.argb((int) (this.blinkAlpha * 255.0f), 255, 255, 255));
        }
        if (this.stage == 5 || this.stage == 6) {
            this.picPaint.setAlpha((int) (255.0f * this.boardBlinkAlpha));
            canvas.drawBitmap(this.boardBlinkBitmap, (Rect) null, new Rect(0, 0, this.SCREEN_WIDTH, this.SCREEN_HEIGHT), this.picPaint);
        }
        if ((this.stage == 5 || this.stage == 6) && this.musicOffset > this.startBoardBlinkOffset) {
            int cof = this.musicOffset - this.startBoardBlinkOffset;
            int interval = (int) (60000.0f / this.musicBPM);
            float f2 = cof % interval;
            if (f2 < (interval / 9) * 10) {
                f = (((((interval / 9) * 10) - f2) / ((interval / 9) * 10)) / 5.0f) + 0.8f;
            } else {
                f = 1.0f;
            }
            this.picPaint.setAlpha(255);
            Matrix matrix = canvas.getMatrix();
            matrix.postTranslate(this.satoriBlinkX, this.satoriBlinkY);
            matrix.postRotate(this.satoriBlinkAngel, this.satoriBlinkX + this.satoriBlinkWidth, this.satoriBlinkY + this.satoriBlinkHeight);
            matrix.postScale(f / 1.4f, f / 1.4f, this.satoriBlinkX + this.satoriBlinkWidth, this.satoriBlinkY + this.satoriBlinkHeight);
            canvas.drawBitmap(this.satoriBlinkBitmap, matrix, this.picPaint);
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (event.getAction() != 0 || this.stage != 6) {
            return true;
        }
        if (x > this.buttonStartX - (this.buttonWidth / 2.0f) && x < this.buttonStartX + (this.buttonWidth / 2.0f) && y > this.buttonStartY - (this.buttonHeight / 2.0f) && y < this.buttonStartY + (this.buttonHeight / 2.0f)) {
            this.context.start();
            return true;
        }
        if (x > this.buttonExitX - (this.buttonWidth / 2.0f) && x < this.buttonExitX + (this.buttonWidth / 2.0f) && y > this.buttonExitY - (this.buttonHeight / 2.0f) && y < this.buttonExitY + (this.buttonHeight / 2.0f)) {
            this.context.exit();
            return true;
        }
        return true;
    }

    public int getStage() {
        return this.stage;
    }

    @Override // local.kcn.view.BaseSurfaceView
    protected void release() {
        if (this.mp != null) {
            if (this.mp.isPlaying()) {
                this.mp.stop();
            }
            this.mp.release();
            this.mp = null;
            LogUtil.log("release");
        }
    }
}
