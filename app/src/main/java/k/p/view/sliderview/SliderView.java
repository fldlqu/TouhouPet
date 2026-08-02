package k.p.view.sliderview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import k.p.action.SleepAction;
import k.p.action.StudyAction;
import k.p.action.WakeUpAction;
import k.p.action.WorkAction;
import k.p.domain.BasePet;
import k.p.domain.states.ActiveState;
import k.p.domain.states.DeadState;
import k.p.location.Location;
import k.p.main.MainService;
import k.p.main.R;
import k.p.services.DialogService;
import k.p.services.LocationService;
import k.p.services.StateService;
import k.p.services.ViewService;
import k.p.song.SongService;
import k.p.view.BaseDesktopView;
import k.p.view.sliderview.BarrageSliderItemList;

/* JADX INFO: loaded from: classes.dex */
public class SliderView extends BaseDesktopView {
    protected SliderItemList actionList;
    protected List<SliderItemList> allList;
    protected SliderItemList barrageList;
    protected Bitmap bgBitmap;
    protected SliderItemList currentSliderItemList;
    protected Rect drawRect;
    protected SliderItemList itemList;
    public Map<Location, LocationSliderItemList> locationMap;
    protected SliderItemList mainList;
    protected MainService mainService;
    protected SliderItemList settingsList;
    protected SliderItemList studyList;
    protected Bitmap textBGBitmap;
    protected Bitmap textButtonBGBitmap;
    protected Bitmap titleTextBGBitmap;
    protected SliderItemList workList;

    public void refreshWork() {
        ((WorkSliderItemList) this.workList).refreshWorkInfo();
    }

    public void addWork(WorkAction.BaseWorkInfo info) {
        ((WorkSliderItemList) this.workList).addWorkInfo(info);
    }

    public void addStudy(StudyAction.BaseStudyInfo info) {
        ((StudySliderItemList) this.studyList).addStudyInfo(info);
    }

    public void refreshStudy() {
        ((StudySliderItemList) this.studyList).refreshStudyInfo();
    }

    public void addBarrage(BarrageSliderItemList.BarrageInfo info) {
        ((BarrageSliderItemList) this.barrageList).addEnemy(info);
    }

    public void refreshBarrage() {
        ((BarrageSliderItemList) this.barrageList).refreshEnemy();
    }

    private void initSliderItemList() {
        SliderCanvas sc = new SliderCanvas() { // from class: k.p.view.sliderview.SliderView.1
            private Canvas canvas = null;

            @Override // k.p.view.sliderview.SliderCanvas
            public void drawBitmap(SliderItemView view, Bitmap bitmap, Rect src, Rect dest, Paint paint) {
                int position = view.getPosition();
                int height = view.getHeight();
                if (paint == null) {
                    paint = SliderView.this.picPaint;
                }
                dest.top = (int) (dest.top * SliderView.this.Y_SCALE);
                dest.bottom = (int) (dest.bottom * SliderView.this.Y_SCALE);
                dest.left = (int) (dest.left * SliderView.this.X_SCALE);
                dest.right = (int) (dest.right * SliderView.this.X_SCALE);
                Rect targetRect = new Rect(dest.left, (int) ((dest.top + position) - SliderView.this.currentSliderItemList.currentPosition), dest.right, (int) ((dest.bottom + position) - SliderView.this.currentSliderItemList.currentPosition));
                this.canvas.clipRect(0, 0, SliderView.this.viewWidth, SliderView.this.viewHeight);
                if (position + height > SliderView.this.currentSliderItemList.currentPosition && position < SliderView.this.currentSliderItemList.currentPosition + SliderView.this.viewHeight && bitmap != null && !bitmap.isRecycled()) {
                    this.canvas.drawBitmap(bitmap, src, targetRect, paint);
                }
            }

            @Override // k.p.view.sliderview.SliderCanvas
            public void drawText(SliderItemView view, String str, int x, int y, Paint paint) {
                int position = view.getPosition();
                Paint tmpPaint = new Paint(paint);
                float buf = 50.0f * SliderView.this.Y_SCALE;
                tmpPaint.setTextSize(paint.getTextSize() * SliderView.this.X_SCALE);
                int y2 = (int) ((((int) (y * SliderView.this.Y_SCALE)) + position) - SliderView.this.currentSliderItemList.currentPosition);
                this.canvas.clipRect(0, 0, SliderView.this.viewWidth, SliderView.this.viewHeight);
                if (y2 > (-buf) && y2 < SliderView.this.SCREEN_HEIGHT + buf) {
                    if (str.length() > 4) {
                        tmpPaint.setTextSize(paint.getTextSize() * 0.6f);
                    }
                    this.canvas.drawText(str, x, y2, tmpPaint);
                }
            }

            @Override // k.p.view.sliderview.SliderCanvas
            public void setCanvas(Canvas canvas) {
                this.canvas = canvas;
            }
        };
        this.mainList = new SliderItemList(this, sc) {
        @Override
        public void init() {
            super.init();
            BaseSliderButton button = new BaseSliderButton(SliderView.this, "属性", R.drawable.button_status) { // from class: k.p.view.sliderview.SliderView.2.1
                @Override // k.p.view.sliderview.BaseSliderButton, k.p.view.sliderview.SliderItemView
                public void onClick() {
                    switch (ViewService.statusView.stage) {
                        case 0:
                            ViewService.statusView.hide();
                            break;
                        case 1:
                            ViewService.statusView.show();
                            break;
                    }
                }
            };
            addSliderItemView(button);
            BaseSliderButton button2 = new BaseSliderButton(SliderView.this, "动作", R.drawable.button_action) { // from class: k.p.view.sliderview.SliderView.2.2
                @Override // k.p.view.sliderview.BaseSliderButton, k.p.view.sliderview.SliderItemView
                public void onClick() {
                    if (!(SliderView.this.mainService.pet.getCurrentState() instanceof DeadState)) {
                        SliderView.this.currentSliderItemList = SliderView.this.actionList;
                    } else {
                        DialogService.alert("TouhouPet", "你的宠物已经被四季带走");
                    }
                }
            };
            addSliderItemView(button2);
            BaseSliderButton button3 = new BaseSliderButton(SliderView.this, "玩弹幕", R.drawable.button_barrage) { // from class: k.p.view.sliderview.SliderView.2.3
                @Override // k.p.view.sliderview.BaseSliderButton, k.p.view.sliderview.SliderItemView
                public void onClick() {
                    if (!(SliderView.this.mainService.pet.getCurrentState() instanceof DeadState)) {
                        SliderView.this.currentSliderItemList = SliderView.this.barrageList;
                    } else {
                        DialogService.alert("TouhouPet", "你的宠物已经被四季带走");
                    }
                }
            };
            addSliderItemView(button3);
            BaseSliderButton button4 = new BaseSliderButton(SliderView.this, "物品", R.drawable.button_item) { // from class: k.p.view.sliderview.SliderView.2.4
                @Override // k.p.view.sliderview.BaseSliderButton, k.p.view.sliderview.SliderItemView
                public void onClick() {
                    if (!(SliderView.this.mainService.pet.getCurrentState() instanceof DeadState)) {
                        SliderView.this.currentSliderItemList = SliderView.this.itemList;
                    } else {
                        DialogService.alert("TouhouPet", "你的宠物已经被四季带走");
                    }
                }
            };
            addSliderItemView(button4);
            BaseSliderButton button5 = new BaseSliderButton(SliderView.this, "出门", R.drawable.button_out) { // from class: k.p.view.sliderview.SliderView.2.5
                @Override // k.p.view.sliderview.BaseSliderButton, k.p.view.sliderview.SliderItemView
                public void onClick() {
                    if (!(SliderView.this.mainService.pet.getCurrentState() instanceof DeadState)) {
                        if (SliderView.this.mainService.pet.getCurrentState() instanceof ActiveState) {
                            Location startLocation = LocationService.findLocationByName("间歇泉");
                            SliderView.this.mainService.pet.setCurrentLocation(startLocation);
                            SliderView.this.currentSliderItemList = SliderView.this.locationMap.get(startLocation);
                            return;
                        }
                        DialogService.confirm("出门", "当前正在" + SliderView.this.mainService.pet.getCurrentState().getStateDoingDescription() + "\r\n确定要出门吗?", new DialogService.CallBack() { // from class: k.p.view.sliderview.SliderView.2.5.1
                            @Override // k.p.services.DialogService.CallBack
                            public void onReturn(boolean retVal) {
                                if (retVal) {
                                    SliderView.this.mainService.pet.requestChangeState(StateService.ACTIVE, BasePet.MAX_LEVEL);
                                    Location startLocation2 = LocationService.findLocationByName("间歇泉");
                                    SliderView.this.mainService.pet.setCurrentLocation(startLocation2);
                                    SliderView.this.currentSliderItemList = SliderView.this.locationMap.get(startLocation2);
                                }
                            }
                        });
                        return;
                    }
                    DialogService.alert("TouhouPet", "你的宠物已经被四季带走");
                }
            };
            addSliderItemView(button5);
            BaseSliderButton button6 = new BaseSliderButton(SliderView.this, "音乐", R.drawable.button_music) { // from class: k.p.view.sliderview.SliderView.2.6
                @Override // k.p.view.sliderview.BaseSliderButton, k.p.view.sliderview.SliderItemView
                public void onClick() {
                    SongService.requestSongMenuView();
                }
            };
            addSliderItemView(button6);
            BaseSliderButton button7 = new BaseSliderButton(SliderView.this, "设定", R.drawable.button_settings) { // from class: k.p.view.sliderview.SliderView.2.7
                @Override // k.p.view.sliderview.BaseSliderButton, k.p.view.sliderview.SliderItemView
                public void onClick() {
                    SliderView.this.currentSliderItemList = SliderView.this.settingsList;
                }
            };
            addSliderItemView(button7);
            BaseSliderButton button8 = new BaseSliderButton(SliderView.this, "隐藏", R.drawable.button_minimize) { // from class: k.p.view.sliderview.SliderView.2.8
                @Override // k.p.view.sliderview.BaseSliderButton, k.p.view.sliderview.SliderItemView
                public void onClick() {
                    try {
                        ViewService.petView.hide();
                    } catch (Exception e) {
                    }
                }
            };
            addSliderItemView(button8);
            BaseSliderButton button9 = new BaseSliderButton(SliderView.this, "退出", R.drawable.button_exit) { // from class: k.p.view.sliderview.SliderView.2.9
                @Override // k.p.view.sliderview.BaseSliderButton, k.p.view.sliderview.SliderItemView
                public void onClick() {
                    DialogService.confirm("退出", "确定退出游戏吗?", new DialogService.CallBack() { // from class: k.p.view.sliderview.SliderView.2.9.1
                        @Override // k.p.services.DialogService.CallBack
                        public void onReturn(boolean retVal) {
                            if (retVal) {
                                SliderView.this.mainService.exit();
                            }
                        }
                    });
                }
            };
            addSliderItemView(button9);
        }
        };
        this.studyList = new StudySliderItemList(this, sc);
        this.workList = new WorkSliderItemList(this, sc);
        this.barrageList = new BarrageSliderItemList(this, sc);
        this.settingsList = new SliderItemList(this, sc) { // from class: k.p.view.sliderview.SliderView.3
            @Override // k.p.view.sliderview.SliderItemList
            public void init() {
                super.init();
                addSliderItemView(new BaseSliderTextButton(SliderView.this, "修改名字") { // from class: k.p.view.sliderview.SliderView.3.1
                    @Override // k.p.view.sliderview.BaseSliderTextButton, k.p.view.sliderview.SliderItemView
                    public void onClick() {
                        DialogService.changeNameDialog();
                    }
                });
                addSliderItemView(new ReturnButton(SliderView.this));
            }
        };
        this.itemList = new ItemSliderItemList(this, sc);
        this.actionList = new SliderItemList(this, sc) { // from class: k.p.view.sliderview.SliderView.4
            @Override // k.p.view.sliderview.SliderItemList
            public void init() {
                super.init();
                BaseSliderButton button = new BaseSliderButton(SliderView.this, "睡觉", R.drawable.button_sleep) { // from class: k.p.view.sliderview.SliderView.4.1
                    @Override // k.p.view.sliderview.BaseSliderButton, k.p.view.sliderview.SliderItemView
                    public void onClick() {
                        if (SliderView.this.mainService.pet.getCurrentLocation() == LocationService.HOME) {
                            SliderView.this.currentSliderItemList = SliderView.this.mainList;
                            SliderView.this.toast((String) new SleepAction().doAction(SliderView.this.mainService.pet, null).getStatus());
                            return;
                        }
                        DialogService.alert("睡觉", "只有在家里才能睡觉...");
                    }
                };
                addSliderItemView(button);
                BaseSliderButton button2 = new BaseSliderButton(SliderView.this, "起床", R.drawable.button_wakeup) { // from class: k.p.view.sliderview.SliderView.4.2
                    @Override // k.p.view.sliderview.BaseSliderButton, k.p.view.sliderview.SliderItemView
                    public void onClick() {
                        SliderView.this.currentSliderItemList = SliderView.this.mainList;
                        SliderView.this.toast((String) new WakeUpAction().doAction(SliderView.this.mainService.pet, null).getStatus());
                    }
                };
                addSliderItemView(button2);
                BaseSliderButton button3 = new BaseSliderButton(SliderView.this, "学习", R.drawable.button_study) { // from class: k.p.view.sliderview.SliderView.4.3
                    @Override // k.p.view.sliderview.BaseSliderButton, k.p.view.sliderview.SliderItemView
                    public void onClick() {
                        SliderView.this.currentSliderItemList = SliderView.this.studyList;
                    }
                };
                addSliderItemView(button3);
                BaseSliderButton button4 = new BaseSliderButton(SliderView.this, "工作", R.drawable.button_work) { // from class: k.p.view.sliderview.SliderView.4.4
                    @Override // k.p.view.sliderview.BaseSliderButton, k.p.view.sliderview.SliderItemView
                    public void onClick() {
                        SliderView.this.currentSliderItemList = SliderView.this.workList;
                    }
                };
                addSliderItemView(button4);
                BaseSliderButton button5 = new ReturnButton(SliderView.this);
                addSliderItemView(button5);
            }
        };
        this.locationMap = new HashMap();
        for (Location location : LocationService.locationList) {
            LocationSliderItemList tmpList = new LocationSliderItemList(location, this, sc);
            this.locationMap.put(location, tmpList);
        }
        if (this.mainService != null && this.mainService.pet.getCurrentLocation() != LocationService.HOME) {
            changeListByLocation(this.mainService.pet.getCurrentLocation());
        }
        if (this.currentSliderItemList == null) {
            this.currentSliderItemList = this.mainList;
            this.mainService.pet.setCurrentLocation(LocationService.HOME);
        }
    }


    public SliderView(Context context) {
        super(context);
        this.mainService = (MainService) context;
    }

    @Override // k.p.view.BaseDesktopView, local.kcn.view.BaseSurfaceView
    @SuppressLint({"HandlerLeak"})
    protected void init() {
        super.init();
        if (this.context != null) {
            this.mainService = (MainService) this.context;
        }
        setCurrentFPS(30.0f);
        setViewHeight((int) (1280.0f * this.Y_SCALE));
        setViewWidth((int) (160.0f * this.X_SCALE));
        setViewCurrentX(-this.viewWidth);
        this.params.x = -this.viewWidth;
        this.bgBitmap = loadBitmap(R.drawable.slider);
        this.textBGBitmap = loadBitmap(R.drawable.text_bg);
        this.textButtonBGBitmap = loadBitmap(R.drawable.textbutton_bg);
        this.titleTextBGBitmap = loadBitmap(R.drawable.title_text_bg);
        this.drawRect = new Rect(0, 0, this.viewWidth - 1, this.viewHeight);
        this.allList = new ArrayList();
        initSliderItemList();
        callSliderItemListInit();
    }

    @Override // local.kcn.view.BaseSurfaceView
    protected void release() {
        super.release();
        for (SliderItemList list : this.allList) {
            list.release();
        }
    }

    @Override // local.kcn.view.BaseSurfaceView
    protected void update(Canvas canvas) {
        canvas.drawPaint(this.clearPaint);
        if (this.bgBitmap != null && !this.bgBitmap.isRecycled()) {
            canvas.drawBitmap(this.bgBitmap, (Rect) null, this.drawRect, this.picPaint);
        }
        drawSliderItemView(canvas);
    }

    private void drawSliderItemView(Canvas canvas) {
        this.currentSliderItemList.sc.setCanvas(canvas);
        if (this.currentSliderItemList == this.itemList) {
            synchronized (this.itemList) {
                for (SliderItemView view : this.currentSliderItemList.sliderItemList) {
                    view.onDraw(this.currentSliderItemList.sc);
                }
            }
            return;
        }
        for (SliderItemView view2 : this.currentSliderItemList.sliderItemList) {
            view2.onDraw(this.currentSliderItemList.sc);
        }
    }

    @Override // k.p.view.BaseDesktopView
    protected void onMove(float x, float y, int time) {
        this.currentSliderItemList.currentPosition -= y;
        this.currentSliderItemList.targetPosition -= y;
    }

    @Override // k.p.view.BaseDesktopView
    protected void onUp(float x, float y) {
        if (this.currentSliderItemList.currentPosition < 0.0f && (-this.currentSliderItemList.currentPosition) + this.currentSliderItemList.itemListHeight > this.viewHeight) {
            this.currentSliderItemList.targetPosition = Math.abs(this.currentSliderItemList.currentPosition) > Math.abs(((float) (this.currentSliderItemList.itemListHeight - this.viewHeight)) - this.currentSliderItemList.currentPosition) ? this.currentSliderItemList.itemListHeight - this.viewHeight : 0;
        } else if (this.currentSliderItemList.currentPosition > this.currentSliderItemList.itemListHeight - this.viewHeight && this.currentSliderItemList.currentPosition > 0.0f) {
            this.currentSliderItemList.targetPosition = Math.abs(this.currentSliderItemList.currentPosition) > Math.abs(((float) (this.currentSliderItemList.itemListHeight - this.viewHeight)) - this.currentSliderItemList.currentPosition) ? this.currentSliderItemList.itemListHeight - this.viewHeight : 0;
        }
    }

    @Override // k.p.view.BaseDesktopView
    protected void onClick(float x, float y) {
        if (DialogService.currentDialogView == null) {
            float realY = y + this.currentSliderItemList.currentPosition;
            for (SliderItemView view : this.currentSliderItemList.sliderItemList) {
                if (realY > view.getPosition() && realY < view.getPosition() + view.getHeight()) {
                    view.onClick();
                    return;
                }
            }
        }
    }

    @Override // k.p.view.BaseDesktopView
    protected void onHide() {
        ViewService.statusView.hideImmediately();
        setViewCurrentX(-this.viewWidth);
        this.params.x = -this.viewWidth;
        if (SongService.songMenuViewShow) {
            SongService.requestSongMenuView();
        }
    }

    @Override // k.p.view.BaseDesktopView
    protected void onShow() {
    }

    @Override // k.p.view.BaseDesktopView
    protected void onHideAsync() {
        for (SliderItemList list : this.allList) {
            list.onHide();
        }
    }

    @Override // k.p.view.BaseDesktopView
    protected void onShowAsync() {
        for (SliderItemList list : this.allList) {
            list.onShow();
        }
    }

    @Override // k.p.view.BaseDesktopView, local.kcn.view.BaseSurfaceView
    protected boolean updateStatus(int time) {
        super.updateStatus(time);
        if (this.currentSliderItemList == null) {
            return false;
        }
        float distance = Math.abs(this.currentSliderItemList.currentPosition - this.currentSliderItemList.targetPosition);
        if (distance > 2.0f) {
            SliderItemList sliderItemList = this.currentSliderItemList;
            sliderItemList.currentPosition = (this.currentSliderItemList.currentPosition > this.currentSliderItemList.targetPosition ? ((-distance) / 10.0f) - 2.0f : (distance / 10.0f) + 2.0f) + sliderItemList.currentPosition;
        } else {
            this.currentSliderItemList.currentPosition = this.currentSliderItemList.targetPosition;
        }
        return this.params.x != (-this.viewWidth);
    }

    private void callSliderItemListInit() {
        for (SliderItemList list : this.allList) {
            list.init();
            for (SliderItemView view : list.sliderItemList) {
                view.init();
            }
        }
    }

    protected float getYScale() {
        return this.Y_SCALE;
    }

    public void returnToMainList() {
        this.currentSliderItemList = this.mainList;
    }

    public void changeListByLocation(Location targetLocation) {
        this.currentSliderItemList = this.locationMap.get(targetLocation);
    }
}
