package local.kcn.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import java.io.FileDescriptor;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import local.kcn.utils.LogUtil;

/* JADX INFO: loaded from: classes.dex */
public class BaseSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    protected static final float MAX_FPS = 40.0f;
    protected static final float MIN_FPS = 0.01f;
    protected static final String TAG = "BaseSurfaceView";
    protected int SCREEN_HEIGHT;
    protected int SCREEN_WIDTH;
    protected float X_SCALE;
    protected float Y_SCALE;
    private List<Bitmap> bitmapList;
    protected Context context;
    protected float currentFPS;
    protected long currentTime;
    protected Thread drawThread;
    protected int frames;
    protected SurfaceHolder holder;
    protected long lastTime;
    protected boolean loop;
    protected long offset;
    private boolean paused;
    protected Paint picPaint;
    protected Resources res;
    protected long startTime;
    protected int updateInterval;
    protected boolean updateStatusWhenPaused;

    public BaseSurfaceView(Context context) {
        this(context, null);
    }

    /* JADX WARN: Type inference failed for: r0v5, types: [local.kcn.view.BaseSurfaceView$1] */
    public BaseSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.paused = false;
        this.loop = true;
        this.updateStatusWhenPaused = false;
        this.frames = 0;
        this.offset = 0L;
        this.context = context;
        this.res = context.getResources();
        this.holder = getHolder();
        this.holder.setFormat(-3);
        init0();
        new Thread() { // from class: local.kcn.view.BaseSurfaceView.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                BaseSurfaceView.this.asyncInit();
                BaseSurfaceView.this.asyncInitCompleted();
            }
        }.start();
    }

    public void startDraw() {
        this.holder.addCallback(this);
    }

    public void requestStop() {
        this.loop = false;
        try {
            this.drawThread.join(1000L);
        } catch (InterruptedException e) {
            LogUtil.log(e);
        } finally {
            release0();
        }
    }

    public void requestPause() {
        this.paused = true;
    }

    protected void init() {
    }

    protected void asyncInit() {
    }

    protected void asyncInitCompleted() {
    }

    protected boolean updateStatus(int time) {
        return true;
    }

    protected void update(Canvas canvas) {
    }

    protected void release() {
    }

    private void init0() {
        setCurrentFPS(MAX_FPS);
        this.SCREEN_WIDTH = ((WindowManager) this.context.getApplicationContext().getSystemService("window")).getDefaultDisplay().getWidth();
        this.SCREEN_HEIGHT = ((WindowManager) this.context.getApplicationContext().getSystemService("window")).getDefaultDisplay().getHeight();
        this.X_SCALE = this.SCREEN_WIDTH / 720.0f;
        this.Y_SCALE = this.SCREEN_HEIGHT / 1280.0f;
        this.picPaint = new Paint();
        this.drawThread = new Thread() { // from class: local.kcn.view.BaseSurfaceView.2
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                BaseSurfaceView.this.loop();
            }
        };
        this.bitmapList = new ArrayList();
        init();
    }

    private boolean updateStatus0() {
        int time = (int) (this.currentTime - this.lastTime);
        this.frames++;
        this.offset = this.currentTime - this.startTime;
        return updateStatus(time);
    }

    private void update0(Canvas canvas) {
        update(canvas);
    }

    private void release0() {
        for (Bitmap bitmap : this.bitmapList) {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        this.bitmapList.clear();
        release();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loop() {
        this.currentTime = SystemClock.elapsedRealtime();
        this.startTime = this.currentTime;
        while (this.loop) {
            Canvas canvas = null;
            try {
                if (!this.paused || this.updateStatusWhenPaused) {
                    this.lastTime = this.currentTime;
                    this.currentTime = SystemClock.elapsedRealtime();
                    if (updateStatus0()) {
                        synchronized (this.holder) {
                            canvas = this.holder.lockCanvas();
                            if (canvas != null && !this.paused) {
                                try {
                                    update0(canvas);
                                } catch (Exception e) {
                                    LogUtil.log(TAG, e);
                                }
                            }
                        }
                    }
                }
                int minTime = (int) (((long) this.updateInterval) - (this.currentTime - this.lastTime));
                if (minTime > 0) {
                    try {
                        Thread.sleep(minTime);
                    } catch (InterruptedException e2) {
                        LogUtil.log(e2);
                    }
                }
                if (canvas != null) {
                    this.holder.unlockCanvasAndPost(canvas);
                }
            } catch (Throwable th) {
                if (canvas != null) {
                    this.holder.unlockCanvasAndPost(canvas);
                }
                throw th;
            }
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder holder) {
        if (this.paused) {
            this.paused = false;
        } else {
            this.drawThread.start();
        }
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (!this.paused) {
            requestPause();
        }
    }

    protected Bitmap loadBitmap(byte[] data, int offset, int length) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, offset, length);
        this.bitmapList.add(bitmap);
        return bitmap;
    }

    protected Bitmap loadBitmap(byte[] data, int offset, int length, BitmapFactory.Options opts) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, offset, length, opts);
        this.bitmapList.add(bitmap);
        return bitmap;
    }

    protected Bitmap loadBitmap(String pathName) {
        Bitmap bitmap = BitmapFactory.decodeFile(pathName);
        this.bitmapList.add(bitmap);
        return bitmap;
    }

    protected Bitmap loadBitmap(String pathName, BitmapFactory.Options opts) {
        Bitmap bitmap = BitmapFactory.decodeFile(pathName, opts);
        this.bitmapList.add(bitmap);
        return bitmap;
    }

    protected Bitmap loadBitmap(FileDescriptor fd) {
        Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd);
        this.bitmapList.add(bitmap);
        return bitmap;
    }

    protected Bitmap loadBitmap(FileDescriptor fd, Rect outPadding, BitmapFactory.Options opts) {
        Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd, outPadding, opts);
        this.bitmapList.add(bitmap);
        return bitmap;
    }

    protected Bitmap loadBitmap(int id) {
        Bitmap bitmap = BitmapFactory.decodeResource(this.res, id);
        this.bitmapList.add(bitmap);
        return bitmap;
    }

    protected Bitmap loadBitmap(int id, BitmapFactory.Options opts) {
        Bitmap bitmap = BitmapFactory.decodeResource(this.res, id, opts);
        this.bitmapList.add(bitmap);
        return bitmap;
    }

    protected Bitmap loadBitmap(TypedValue value, InputStream is, Rect pad, BitmapFactory.Options opts) {
        Bitmap bitmap = BitmapFactory.decodeResourceStream(this.res, value, is, pad, opts);
        this.bitmapList.add(bitmap);
        return bitmap;
    }

    protected Bitmap loadBitmap(InputStream is) {
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        this.bitmapList.add(bitmap);
        return bitmap;
    }

    protected Bitmap loadBitmap(InputStream is, Rect outPadding, BitmapFactory.Options opts) {
        Bitmap bitmap = BitmapFactory.decodeStream(is, outPadding, opts);
        this.bitmapList.add(bitmap);
        return bitmap;
    }

    protected void releaseBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            if (this.bitmapList.contains(bitmap)) {
                this.bitmapList.remove(bitmap);
            }
        }
    }

    protected void log(String msg) {
        LogUtil.log(msg);
    }

    protected void log(String tag, String msg) {
        LogUtil.log(tag, msg);
    }

    protected void log(Exception e) {
        LogUtil.log(e);
    }

    public void setDebug(boolean debug) {
        LogUtil.debug = debug;
    }

    public float getCurrentFPS() {
        return this.currentFPS;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void setCurrentFPS(float currentFPS) {
        if (currentFPS > MAX_FPS) {
            currentFPS = MAX_FPS;
        } else if (currentFPS < MIN_FPS) {
            currentFPS = MIN_FPS;
        }
        this.currentFPS = currentFPS;
        this.updateInterval = (int) (1000.0f / currentFPS);
    }
}
