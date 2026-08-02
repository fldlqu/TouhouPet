package k.p.main;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RemoteViews;
import k.p.domain.BasePet;
import k.p.domain.states.DeadState;
import k.p.listener.OnPetPropertyChangeListener;
import k.p.listener.PetPropertyChangeEvent;
import k.p.services.AnimationService;
import k.p.services.BarrageService;
import k.p.services.DesktopService;
import k.p.services.DialogService;
import k.p.services.ItemService;
import k.p.services.ListenerService;
import k.p.services.LocationService;
import k.p.services.PetService;
import k.p.services.StateService;
import k.p.services.ViewService;
import k.p.song.SongService;
import k.p.utils.EnvironmentUtil;
import k.p.utils.SaveLoadUtil;
import k.p.view.PetView;
import k.p.view.SliderHandlerView;
import k.p.view.StatusView;
import k.p.view.sliderview.SliderView;
import local.kcn.utils.LogUtil;

/* JADX INFO: loaded from: classes.dex */
public class MainService extends Service {
    public static final int GAME_SPEED = 1;
    private static final int INTERRUPT = 1;
    private static final int NOTIFICATION_ID = 16;
    private static final int REQUEST_DIALOG = 0;
    private static final int SAVE_INTERVAL = 600000;
    private static final int SHOW_BARRAGEVIEW = 4;
    private static final int SHOW_SONGMENUVIEW = 3;
    private static final int SHOW_SONGVIEW = 2;
    private static final int UPDATE_BARRAGEVIEW = 5;
    public static MainService context;
    private static Notification notification;
    public static NotificationManager notificationManager;
    private long currentTime;
    private long lastTime;
    public BasePet pet;
    private int saveTime;
    private Thread updateThread;
    public WindowManager windowManager;
    private int updateInterval = 200;
    private boolean updateLoop = true;
    private Handler handler = new Handler() { // from class: k.p.main.MainService.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (DialogService.currentDialogView != null) {
                        MainService.this.windowManager.addView(DialogService.currentDialogView, DialogService.currentDialogView.getLayoutParams());
                        return;
                    }
                    return;
                case 1:
                    try {
                        Exception e = (Exception) msg.obj;
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainService.this.getApplicationContext());
                        builder.setTitle("Error");
                        builder.setMessage("游戏异常中止,即将尝试保存退出\r\n\r\nCause By : \r\n" + e.getLocalizedMessage());
                        builder.setPositiveButton("确定", (DialogInterface.OnClickListener) null);
                        AlertDialog dialog = builder.create();
                        dialog.getWindow().setType(2003);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                        return;
                    } catch (Exception e2) {
                        LogUtil.log(e2, false);
                        return;
                    } finally {
                        MainService.this.exit();
                    }
                case 2:
                    MainService.this.windowManager.addView(SongService.songView, SongService.songView.getLayoutParams());
                    return;
                case 3:
                    MainService.this.windowManager.addView(SongService.songMenuView, SongService.songMenuView.getLayoutParams());
                    return;
                case MainService.SHOW_BARRAGEVIEW /* 4 */:
                    MainService.this.windowManager.addView(BarrageService.barrageView, BarrageService.barrageView.getLayoutParams());
                    return;
                case MainService.UPDATE_BARRAGEVIEW /* 5 */:
                    MainService.this.windowManager.updateViewLayout(BarrageService.barrageView, BarrageService.barrageView.getLayoutParams());
                    return;
                default:
                    return;
            }
        }
    };

    private void initLog() {
        LogUtil.recordPath = String.valueOf(EnvironmentUtil.getMainPath()) + "/system/log/exception.thp";
        LogUtil.registerExceptionListener(new LogUtil.ExceptionListener() { // from class: k.p.main.MainService.2
            @Override // local.kcn.utils.LogUtil.ExceptionListener
            public void occurException(Exception e) {
                Message message = new Message();
                message.obj = e;
                message.what = 1;
                MainService.this.handler.sendMessage(message);
            }
        });
    }

    private void initService(MainService mainService) throws Exception {
        DesktopService.init(mainService);
        AnimationService.init(mainService);
        DialogService.init(mainService);
        SongService.loadSong(mainService);
    }

    @Override // android.app.Service
    public void onCreate() {
        context = this;
        this.windowManager = (WindowManager) getApplicationContext().getSystemService("window");
        try {
            initLog();
        } catch (Exception e1) {
            LogUtil.log(e1);
        }
        try {
            initService(this);
        } catch (Exception e12) {
            LogUtil.log(e12);
        }
        try {
        } catch (Exception e13) {
            LogUtil.log(e13);
        }
        if (PetService.pet != null) {
            this.pet = PetService.pet;
            try {
                if (this.pet.getCurrentState() instanceof DeadState) {
                    DialogService.confirm("TouhouPet", "你的宠物已被四季找去喝茶...\r\n\r\n领回需要贿赂四季(宠物等级*10)个P点", "领回", "重新来过", new DialogService.CallBack() { // from class: k.p.main.MainService.3
                        @Override // k.p.services.DialogService.CallBack
                        public void onReturn(boolean retVal) {
                            LogUtil.log("click!");
                            if (retVal) {
                                if (MainService.this.pet.getPower() >= MainService.this.pet.getLevel() * 10) {
                                    MainService.this.pet.changePower((-MainService.this.pet.getLevel()) * 10);
                                    MainService.this.pet.changeEnergy(100);
                                    MainService.this.pet.changeRepletionDegree(100);
                                    MainService.this.pet.changeDrinkDegree(100);
                                    MainService.this.pet.requestChangeState(StateService.ACTIVE, BasePet.MAX_LEVEL);
                                    return;
                                }
                                DialogService.alert("TouhouPet", "你没有足够的P点", new DialogService.CallBack() { // from class: k.p.main.MainService.3.1
                                    @Override // k.p.services.DialogService.CallBack
                                    public void onReturn(boolean retVal2) {
                                        MainService.this.exitWithoutSave();
                                    }
                                });
                                return;
                            }
                            SaveLoadUtil.clear("pet.thp");
                            SaveLoadUtil.clear("item.thp");
                            PetService.pet = null;
                            MainService.this.exitWithoutSave();
                        }
                    });
                }
                ViewService.sliderView = new SliderView(this);
                ViewService.sliderHandlerView = new SliderHandlerView(this);
                ViewService.petView = new PetView(this);
                ViewService.statusView = new StatusView(this);
                ViewService.petView.show();
                notificationManager = (NotificationManager) getSystemService("notification");
                notification = new Notification(R.drawable.satori_blink, "TouhouPet", System.currentTimeMillis());
                notification.flags = 2;
                Intent intent = new Intent();
                intent.setAction("thp");
                notification.contentView = new RemoteViews(getPackageName(), R.layout.notification);
                notification.contentIntent = PendingIntent.getBroadcast(this, 0, intent, 134217728);
                startForeground(NOTIFICATION_ID, notification);
                this.currentTime = SystemClock.elapsedRealtime();
                this.lastTime = this.currentTime;
                this.saveTime = SAVE_INTERVAL;
                this.updateThread = new Thread() { // from class: k.p.main.MainService.4
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        while (MainService.this.updateLoop) {
                            MainService.this.lastTime = MainService.this.currentTime;
                            MainService.this.currentTime = SystemClock.elapsedRealtime();
                            int time = (int) (MainService.this.currentTime - MainService.this.lastTime);
                            MainService.this.pet.addLifeTime(time * 1);
                            MainService.this.saveTime -= time;
                            if (MainService.this.saveTime <= 0) {
                                MainService.this.saveTime = MainService.SAVE_INTERVAL;
                                MainService.this.save();
                            }
                            int sleepTime = MainService.this.updateInterval - time;
                            if (sleepTime > 0) {
                                try {
                                    Thread.sleep(sleepTime);
                                } catch (InterruptedException e) {
                                    LogUtil.log(e);
                                }
                            }
                        }
                    }
                };
                this.updateThread.start();
                ListenerService.registerListener(new OnPetPropertyChangeListener() { // from class: k.p.main.MainService.5
                    @Override // k.p.listener.OnPetPropertyChangeListener
                    public void onPetPropertyChange(PetPropertyChangeEvent event) {
                        if (event.getProperty() != 3) {
                            MainService.updateNotification();
                        }
                    }
                });
                updateNotification();
                return;
            } catch (Exception e14) {
                LogUtil.log(e14);
                return;
            }
        }
        LogUtil.log("cannot get pet");
        exit();
    }

    public static void updateNotification() {
        if (notification != null && PetService.pet != null) {
            notification.contentView.setTextViewText(R.id.notification_tv_state, "状态:" + PetService.pet.getCurrentState().getStateDoingDescription());
            notification.contentView.setTextViewText(R.id.notification_tv_rd, "饱食:" + PetService.pet.getRepletionDegree() + "/100");
            notification.contentView.setTextViewText(R.id.notification_tv_dd, "饮水:" + PetService.pet.getDrinkDegree() + "/100");
            notification.contentView.setTextViewText(R.id.notification_tv_en, "精力:" + PetService.pet.getEnergy() + "/100");
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    public void requestNewDialog() {
        this.handler.sendEmptyMessage(0);
    }

    public void showSongView() {
        this.handler.sendEmptyMessage(2);
    }

    public void showSongMenuView() {
        this.handler.sendEmptyMessage(3);
    }

    public void hideSongView() {
        this.windowManager.removeView(SongService.songView);
    }

    public void hideSongMenuView() {
        this.windowManager.removeView(SongService.songMenuView);
    }

    public void requestNewBarrageView() {
        this.handler.sendEmptyMessage(SHOW_BARRAGEVIEW);
    }

    public void updateBarrageView() {
        this.handler.sendEmptyMessage(UPDATE_BARRAGEVIEW);
    }

    public void removeBarrageView() {
        this.windowManager.removeView(BarrageService.barrageView);
    }

    public void exit() {
        SongService.exit();
        this.updateLoop = false;
        if (this.updateThread != null) {
            try {
                this.updateThread.join(2000L);
            } catch (InterruptedException e) {
            }
        }
        if (this.pet != null) {
            if (this.pet.getCurrentState() != null) {
                this.pet.getCurrentState().onPause();
            }
            save();
        }
        releaseService();
        if (notificationManager != null) {
            notificationManager.cancel(NOTIFICATION_ID);
        }
        stopSelf();
    }

    public void exitWithoutSave() {
        this.updateLoop = false;
        try {
            this.updateThread.join(2000L);
        } catch (InterruptedException e) {
        }
        this.pet.getCurrentState().onPause();
        releaseService();
        notificationManager.cancel(NOTIFICATION_ID);
        stopSelf();
    }

    private void releaseService() {
        DesktopService.release();
        AnimationService.release();
        BarrageService.release();
        DialogService.release();
        ItemService.release();
        ListenerService.release();
        LocationService.release();
        StateService.release();
        ViewService.release();
        PetService.pet = null;
    }

    public void save() {
        try {
            this.pet.onSave();
            SaveLoadUtil.save(this.pet, "pet.thp");
            LogUtil.log("save success");
        } catch (Exception e) {
            LogUtil.log("save fail");
            LogUtil.log(e, false);
        }
    }

    public void load() {
        try {
            this.pet = (BasePet) SaveLoadUtil.load(BasePet.class, "pet.thp");
        } catch (Exception e) {
            Log.e("LOG", "load pet fail");
            LogUtil.log(e, false);
        }
        if (this.pet != null) {
            this.pet.onLoad();
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onDestroy() {
        LogUtil.log("Service is destroy");
    }
}
