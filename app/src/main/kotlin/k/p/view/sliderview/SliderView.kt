package k.p.view.sliderview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.WindowManager
import k.p.action.SleepAction
import k.p.action.StudyAction
import k.p.action.WakeUpAction
import k.p.action.WorkAction
import k.p.domain.BasePet
import k.p.domain.states.ActiveState
import k.p.domain.states.DeadState
import k.p.location.Location
import k.p.main.MainService
import k.p.main.R
import k.p.modern.ShakeMove
import k.p.services.DialogService
import k.p.services.LocationService
import k.p.services.StateService
import k.p.services.ViewService
import k.p.song.SongService
import k.p.view.BaseDesktopView
import local.kcn.view.BaseView
import java.util.ArrayList
import java.util.HashMap

open class SliderView : BaseDesktopView {
    @JvmField
    var actionList: SliderItemList? = null
    @JvmField
    var allList: MutableList<SliderItemList> = ArrayList()
    @JvmField
    var barrageList: SliderItemList? = null
    @JvmField
    var bgBitmap: Bitmap? = null
    @JvmField
    var currentSliderItemList: SliderItemList? = null
    @JvmField
    var drawRect: Rect? = null
    @JvmField
    var itemList: SliderItemList? = null
    @JvmField
    var locationMap: Map<Location, LocationSliderItemList> = HashMap()
    @JvmField
    var mainList: SliderItemList? = null
    @JvmField
    var mainService: MainService? = null
    @JvmField
    var settingsList: SliderItemList? = null
    @JvmField
    var studyList: SliderItemList? = null
    @JvmField
    var textBGBitmap: Bitmap? = null
    @JvmField
    var textButtonBGBitmap: Bitmap? = null
    @JvmField
    var titleTextBGBitmap: Bitmap? = null
    @JvmField
    var workList: SliderItemList? = null

    constructor(context: Context) : super(context) {
        mainService = context as MainService
    }

    fun refreshWork() {
        (workList as WorkSliderItemList).refreshWorkInfo()
    }

    /* 摇晃移动开关状态(thp_prefs 持久化, 默认关) */
    private fun isShakeEnabled(): Boolean {
        return context.getSharedPreferences("thp_prefs", Context.MODE_PRIVATE)
            .getBoolean("shake_move", false)
    }

    fun addWork(info: WorkAction.BaseWorkInfo) {
        (workList as WorkSliderItemList).addWorkInfo(info)
    }

    fun addStudy(info: StudyAction.BaseStudyInfo) {
        (studyList as StudySliderItemList).addStudyInfo(info)
    }

    fun refreshStudy() {
        (studyList as StudySliderItemList).refreshStudyInfo()
    }

    fun addBarrage(info: BarrageSliderItemList.BarrageInfo) {
        (barrageList as BarrageSliderItemList).addEnemy(info)
    }

    fun refreshBarrage() {
        (barrageList as BarrageSliderItemList).refreshEnemy()
    }

    private fun initSliderItemList() {
        val sc: SliderCanvas = object : SliderCanvas {
            private var canvas: Canvas? = null

            override fun drawBitmap(view: SliderItemView, bitmap: Bitmap, src: Rect?, dest: Rect?, paint: Paint?) {
                val position = view.getPosition()
                val height = view.getHeight()
                var p = paint
                if (p == null) {
                    p = this@SliderView.picPaint
                }
                dest!!.top = (dest.top * this@SliderView.Y_SCALE).toInt()
                dest.bottom = (dest.bottom * this@SliderView.Y_SCALE).toInt()
                dest.left = (dest.left * this@SliderView.X_SCALE).toInt()
                dest.right = (dest.right * this@SliderView.X_SCALE).toInt()
                val targetRect = Rect(
                    dest.left,
                    (dest.top + position - this@SliderView.currentSliderItemList!!.currentPosition).toInt(),
                    dest.right,
                    (dest.bottom + position - this@SliderView.currentSliderItemList!!.currentPosition).toInt()
                )
                canvas!!.clipRect(0, 0, this@SliderView.viewWidth, this@SliderView.viewHeight)
                if (position + height > this@SliderView.currentSliderItemList!!.currentPosition &&
                    position < this@SliderView.currentSliderItemList!!.currentPosition + this@SliderView.viewHeight &&
                    bitmap != null && !bitmap.isRecycled
                ) {
                    canvas!!.drawBitmap(bitmap, src, targetRect, p)
                }
            }

            override fun setCanvas(canvas: Canvas?) {
                this.canvas = canvas
            }

            override fun drawText(view: SliderItemView, str: String, x: Int, y: Int, paint: Paint?) {
                val position = view.getPosition()
                val tmpPaint = Paint(paint)
                val buf = 50.0f * this@SliderView.Y_SCALE
                tmpPaint.textSize = paint!!.textSize * this@SliderView.X_SCALE
                val y2 = ((y * this@SliderView.Y_SCALE).toInt() + position - this@SliderView.currentSliderItemList!!.currentPosition).toInt()
                canvas!!.clipRect(0, 0, this@SliderView.viewWidth, this@SliderView.viewHeight)
                if (y2 > -buf && y2 < this@SliderView.SCREEN_HEIGHT + buf) {
                    /* 长文本按实测宽度适配背景(Rect(10,10,150,60)可用宽 140*X_SCALE),
                     * 不硬缩 60%: 原逻辑用未乘 X_SCALE 的字号打折, 高分屏长文本只有 19px */
                    val maxWidth = 140.0f * this@SliderView.X_SCALE
                    val measured = tmpPaint.measureText(str)
                    if (measured > maxWidth) {
                        tmpPaint.textSize = tmpPaint.textSize * (maxWidth / measured)
                    }
                    canvas!!.drawText(str, x.toFloat(), y2.toFloat(), tmpPaint)
                }
            }
        }
        mainList = object : SliderItemList(this@SliderView, sc) {
            override fun init() {
                super.init()
                addSliderItemView(object : BaseSliderButton(this@SliderView, "属性", R.drawable.button_status) {
                    override fun onClick() {
                        when (ViewService.statusView!!.stage) {
                            0 -> ViewService.statusView!!.hide()
                            1 -> ViewService.statusView!!.show()
                        }
                    }
                })
                addSliderItemView(object : BaseSliderButton(this@SliderView, "动作", R.drawable.button_action) {
                    override fun onClick() {
                        if (this@SliderView.mainService!!.pet!!.currentState !is DeadState) {
                            this@SliderView.currentSliderItemList = this@SliderView.actionList
                        } else {
                            DialogService.alert("TouhouPet", "你的宠物已经被四季带走")
                        }
                    }
                })
                addSliderItemView(object : BaseSliderButton(this@SliderView, "玩弹幕", R.drawable.button_barrage) {
                    override fun onClick() {
                        if (this@SliderView.mainService!!.pet!!.currentState !is DeadState) {
                            this@SliderView.currentSliderItemList = this@SliderView.barrageList
                        } else {
                            DialogService.alert("TouhouPet", "你的宠物已经被四季带走")
                        }
                    }
                })
                addSliderItemView(object : BaseSliderButton(this@SliderView, "物品", R.drawable.button_item) {
                    override fun onClick() {
                        if (this@SliderView.mainService!!.pet!!.currentState !is DeadState) {
                            this@SliderView.currentSliderItemList = this@SliderView.itemList
                        } else {
                            DialogService.alert("TouhouPet", "你的宠物已经被四季带走")
                        }
                    }
                })
                addSliderItemView(object : BaseSliderButton(this@SliderView, "出门", R.drawable.button_out) {
                    override fun onClick() {
                        val ms = this@SliderView.mainService!!
                        val mp = ms.pet!!
                        if (mp.currentState !is DeadState) {
                            if (mp.currentState is ActiveState) {
                                val startLocation = LocationService.findLocationByName("间歇泉")
                                mp.setCurrentLocation(startLocation!!)
                                this@SliderView.currentSliderItemList = this@SliderView.locationMap!![startLocation]
                                return
                            }
                            DialogService.confirm(
                                "出门",
                                "当前正在" + mp.currentState.getStateDoingDescription() + "\r\n确定要出门吗?",
                                object : DialogService.CallBack {
                                    override fun onReturn(retVal: Boolean) {
                                        if (retVal) {
                                            mp.requestChangeState(StateService.ACTIVE, BasePet.MAX_LEVEL)
                                            val startLocation2 = LocationService.findLocationByName("间歇泉")
                                            mp.setCurrentLocation(startLocation2!!)
                                            this@SliderView.currentSliderItemList = this@SliderView.locationMap!![startLocation2]
                                        }
                                    }
                                })
                            return
                        }
                        DialogService.alert("TouhouPet", "你的宠物已经被四季带走")
                    }
                })
                addSliderItemView(object : BaseSliderButton(this@SliderView, "音乐", R.drawable.button_music) {
                    override fun onClick() {
                        SongService.requestSongMenuView()
                    }
                })
                addSliderItemView(object : BaseSliderButton(this@SliderView, "设定", R.drawable.button_settings) {
                    override fun onClick() {
                        this@SliderView.currentSliderItemList = this@SliderView.settingsList
                    }
                })
                addSliderItemView(object : BaseSliderButton(this@SliderView, "隐藏", R.drawable.button_minimize) {
                    override fun onClick() {
                        try {
                            ViewService.petView!!.hide()
                        } catch (e: Exception) {
                        }
                    }
                })
                addSliderItemView(object : BaseSliderButton(this@SliderView, "退出", R.drawable.button_exit) {
                    override fun onClick() {
                        DialogService.confirm("退出", "确定退出游戏吗?", object : DialogService.CallBack {
                            override fun onReturn(retVal: Boolean) {
                                if (retVal) {
                                    this@SliderView.mainService!!.exit()
                                }
                            }
                        })
                    }
                })
            }
        }
        studyList = StudySliderItemList(this@SliderView, sc)
        workList = WorkSliderItemList(this@SliderView, sc)
        barrageList = BarrageSliderItemList(this@SliderView, sc)
        settingsList = object : SliderItemList(this@SliderView, sc) {
            override fun init() {
                super.init()
                addSliderItemView(object : BaseSliderTextButton(this@SliderView, "修改名字") {
                    override fun onClick() {
                        DialogService.changeNameDialog()
                    }
                })
                /* 摇晃移动开关(持久化 thp_prefs; 开启后摇晃手机宠物随机换位置) */
                addSliderItemView(object : BaseSliderTextButton(this@SliderView, "") {
                    private val shakeHint: String
                        get() = if (isShakeEnabled()) "摇晃移动:开" else "摇晃移动:关"

                    override fun init() {
                        super.init()
                        setHint(shakeHint)
                    }

                    override fun onClick() {
                        val enabled = !isShakeEnabled()
                        if (enabled && !ShakeMove.setEnabled(this@SliderView.context.applicationContext, true)) {
                            this@SliderView.toast("设备不支持摇晃检测")
                            return
                        }
                        this@SliderView.context.getSharedPreferences("thp_prefs", Context.MODE_PRIVATE)
                            .edit().putBoolean("shake_move", enabled).apply()
                        if (!enabled) {
                            ShakeMove.setEnabled(this@SliderView.context.applicationContext, false)
                        }
                        setHint(shakeHint)
                        this@SliderView.toast(if (enabled) "摇晃移动:开(摇晃手机宠物换位置)" else "摇晃移动:关")
                    }
                })
                /* 帧率上限: 跟随系统(system-preferred) / 40 / 60 轮换。
                 * system 取系统刷新率(defaultDisplay.refreshRate), 120Hz 屏可到 120fps。 */
                addSliderItemView(object : BaseSliderTextButton(this@SliderView, "") {
                    private val modeList = arrayOf("system", "40", "60")
                    private val fpsHint: String
                        get() {
                            return when (getFpsPref()) {
                                "40" -> "帧率:40"
                                "60" -> "帧率:60"
                                else -> "帧率:跟随系统"
                            }
                        }

                    override fun init() {
                        super.init()
                        setHint(fpsHint)
                    }

                    override fun onClick() {
                        val pref = getFpsPref()
                        val curIdx = modeList.indexOf(pref).let { if (it < 0) 0 else it }
                        val next = modeList[(curIdx + 1) % modeList.size]
                        this@SliderView.context.getSharedPreferences("thp_prefs", Context.MODE_PRIVATE)
                            .edit().putString("frame_rate_max", next).apply()
                        applyFrameRateCeiling()
                        setHint(fpsHint)
                        this@SliderView.toast("帧率:" + (BaseView.MAX_FPS.toInt()) + "fps")
                    }
                })
                addSliderItemView(ReturnButton(this@SliderView))
            }

            private fun getFpsPref(): String {
                return this@SliderView.context.getSharedPreferences("thp_prefs", Context.MODE_PRIVATE)
                    .getString("frame_rate_max", "system") ?: "system"
            }
        }
        itemList = ItemSliderItemList(this@SliderView, sc)
        actionList = object : SliderItemList(this@SliderView, sc) {
            override fun init() {
                super.init()
                addSliderItemView(object : BaseSliderButton(this@SliderView, "睡觉", R.drawable.button_sleep) {
                    override fun onClick() {
                        if (this@SliderView.mainService!!.pet!!.currentLocation == LocationService.HOME) {
                            this@SliderView.currentSliderItemList = this@SliderView.mainList
                            val status = SleepAction<Void>().doAction(this@SliderView.mainService!!.pet!!, null)
                            this@SliderView.toast(status!!.status as? String ?: "")
                            return
                        }
                        DialogService.alert("睡觉", "只有在家里才能睡觉...")
                    }
                })
                addSliderItemView(object : BaseSliderButton(this@SliderView, "起床", R.drawable.button_wakeup) {
                    override fun onClick() {
                        this@SliderView.currentSliderItemList = this@SliderView.mainList
                        val status = WakeUpAction<Void>().doAction(this@SliderView.mainService!!.pet!!, null)
                        this@SliderView.toast(status!!.status as? String ?: "")
                    }
                })
                addSliderItemView(object : BaseSliderButton(this@SliderView, "学习", R.drawable.button_study) {
                    override fun onClick() {
                        this@SliderView.currentSliderItemList = this@SliderView.studyList
                    }
                })
                addSliderItemView(object : BaseSliderButton(this@SliderView, "工作", R.drawable.button_work) {
                    override fun onClick() {
                        this@SliderView.currentSliderItemList = this@SliderView.workList
                    }
                })
                addSliderItemView(ReturnButton(this@SliderView))
            }
        }
        locationMap = HashMap<Location, LocationSliderItemList>()
        for (location in LocationService.locationList!!) {
            val tmpList = LocationSliderItemList(location, this@SliderView, sc)
            (locationMap as MutableMap<Location, LocationSliderItemList>)[location] = tmpList
        }
        val ms = mainService
        if (ms != null && ms.pet!!.currentLocation != LocationService.HOME) {
            changeListByLocation(ms.pet!!.currentLocation)
        }
        if (currentSliderItemList == null) {
            currentSliderItemList = mainList
            ms!!.pet!!.currentLocation = LocationService.HOME
        }
    }

    override fun init() {
        super.init()
        if (context != null) {
            mainService = context as MainService
        }
        setCurrentFPS(30.0f)
        setViewHeight((1280.0f * Y_SCALE).toInt())
        setViewWidth((160.0f * X_SCALE).toInt())
        setViewCurrentX(-viewWidth.toFloat())
        params!!.x = -viewWidth
        bgBitmap = loadBitmap(R.drawable.slider)
        textBGBitmap = loadBitmap(R.drawable.text_bg)
        textButtonBGBitmap = loadBitmap(R.drawable.textbutton_bg)
        titleTextBGBitmap = loadBitmap(R.drawable.title_text_bg)
        drawRect = Rect(0, 0, viewWidth - 1, viewHeight)
        allList = ArrayList()
        initSliderItemList()
        callSliderItemListInit()
    }

    override fun release() {
        super.release()
        for (list in allList) {
            list.release()
        }
    }

    override fun update(canvas: Canvas) {
        canvas.drawPaint(clearPaint!!)
        if (bgBitmap != null && !bgBitmap!!.isRecycled) {
            canvas.drawBitmap(bgBitmap!!, null, drawRect!!, picPaint)
        }
        drawSliderItemView(canvas)
    }

    private fun drawSliderItemView(canvas: Canvas) {
        val list = currentSliderItemList!!
        list.sc.setCanvas(canvas)
        if (list === itemList) {
            synchronized(this) {
                for (view in list.sliderItemList) {
                    view.onDraw(list.sc)
                }
            }
            return
        }
        for (view in list.sliderItemList) {
            view.onDraw(list.sc)
        }
    }

    override fun onMove(x: Float, y: Float, time: Int) {
        currentSliderItemList!!.currentPosition -= y
        currentSliderItemList!!.targetPosition -= y
    }

    override fun onUp(x: Float, y: Float) {
        val list = currentSliderItemList!!
        if (list.currentPosition < 0.0f && (-list.currentPosition) + list.itemListHeight > viewHeight) {
            list.targetPosition = if (Math.abs(list.currentPosition) > Math.abs((list.itemListHeight - viewHeight).toFloat() - list.currentPosition)) {
                (list.itemListHeight - viewHeight).toFloat()
            } else {
                0f
            }
        } else if (list.currentPosition > list.itemListHeight - viewHeight && list.currentPosition > 0.0f) {
            list.targetPosition = if (Math.abs(list.currentPosition) > Math.abs((list.itemListHeight - viewHeight).toFloat() - list.currentPosition)) {
                (list.itemListHeight - viewHeight).toFloat()
            } else {
                0f
            }
        }
    }

    override fun onClick(x: Float, y: Float) {
        if (DialogService.currentDialogView == null) {
            val realY = y + currentSliderItemList!!.currentPosition
            for (view in currentSliderItemList!!.sliderItemList) {
                if (realY > view.getPosition() && realY < view.getPosition() + view.getHeight()) {
                    view.onClick()
                    return
                }
            }
        }
    }

    override fun hide() {
        super.hide()
    }

    override fun onHide() {
        ViewService.statusView!!.hideImmediately()
        setViewCurrentX(-viewWidth.toFloat())
        params!!.x = -viewWidth
        if (SongService.songMenuViewShow) {
            SongService.requestSongMenuView()
        }
    }

    override fun onShow() {
    }

    override fun onHideAsync() {
        for (list in allList) {
            list.onHide()
        }
    }

    override fun onShowAsync() {
        for (list in allList) {
            list.onShow()
        }
    }

    override fun updateStatus(time: Int): Boolean {
        super.updateStatus(time)
        val list = currentSliderItemList ?: return false
        val distance = Math.abs(list.currentPosition - list.targetPosition)
        if (distance > 2.0f) {
            list.currentPosition = if (list.currentPosition > list.targetPosition) {
                ((-distance) / 10.0f) - 2.0f + list.currentPosition
            } else {
                (distance / 10.0f) + 2.0f + list.currentPosition
            }
        } else {
            list.currentPosition = list.targetPosition
        }
        return params!!.x != (-viewWidth)
    }

    private fun callSliderItemListInit() {
        for (list in allList) {
            list.init()
            for (view in list.sliderItemList) {
                view.init()
            }
        }
    }

    fun getYScale(): Float = Y_SCALE

    fun returnToMainList() {
        currentSliderItemList = mainList
    }

    fun changeListByLocation(targetY: Location) {
        currentSliderItemList = locationMap[targetY]
    }

    /* 帧率档位的全局应用:
     * - system: BaseView.MAX_FPS = 系统刷新率(60/90/120Hz), 所有 view 目标帧率同步为系统刷新率
     * - 40/60: 所有 view 目标帧率固定为该值
     * 设定切换与启动时(MainService 创建完 4 个 view)调用。 */
    fun applyFrameRateCeiling() {
        val pref = context.getSharedPreferences("thp_prefs", Context.MODE_PRIVATE)
            .getString("frame_rate_max", "system") ?: "system"
        val fps = if (pref == "40") {
            40.0f
        } else if (pref == "60") {
            60.0f
        } else {
            /* system-preferred: 跟随系统刷新率; 异常值回退 40 */
            val rate = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
                .defaultDisplay.refreshRate
            if (rate in 1f..240f) rate else 40.0f
        }
        BaseView.MAX_FPS = fps
        ViewService.petView?.setCurrentFPS(fps)
        ViewService.statusView?.setCurrentFPS(fps)
        ViewService.sliderView?.setCurrentFPS(fps)
        ViewService.sliderHandlerView?.setCurrentFPS(fps)
    }
}