package k.p.main

import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.content.res.Resources
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.SystemClock
import android.util.Log
import android.view.WindowManager
import android.widget.RemoteViews
import k.p.domain.BasePet
import k.p.domain.states.DeadState
import k.p.listener.OnPetPropertyChangeListener
import k.p.listener.PetPropertyChangeEvent
import k.p.modern.DevReceiver
import k.p.modern.Diag
import k.p.services.AnimationService
import k.p.services.BarrageService
import k.p.services.DesktopService
import k.p.services.DialogService
import k.p.services.ItemService
import k.p.services.ListenerService
import k.p.services.LocationService
import k.p.services.PetService
import k.p.services.StateService
import k.p.services.ViewService
import k.p.song.SongService
import k.p.utils.EnvironmentUtil
import k.p.utils.SaveLoadUtil
import k.p.view.PetView
import k.p.view.SliderHandlerView
import k.p.view.StatusView
import k.p.view.sliderview.SliderView
import local.kcn.utils.LogUtil

open class MainService : Service() {
    @JvmField
    var pet: BasePet? = null
    @JvmField
    var windowManager: WindowManager? = null
    private var currentTime = 0L
    private var lastTime = 0L
    private var saveTime = 0
    private var gameLoop: GameLoop? = null
    private var updateInterval = 200

    val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0 -> {
                    if (DialogService.currentDialogView != null) {
                        this@MainService.windowManager!!.addView(DialogService.currentDialogView, DialogService.currentDialogView!!.layoutParams)
                        return
                    }
                    return
                }
                1 -> {
                    try {
                        val e = msg.obj as Exception
                        val builder = AlertDialog.Builder(this@MainService.applicationContext)
                        builder.setTitle("Error")
                        builder.setMessage("游戏异常中止,即将尝试保存退出\r\n\r\nCause By : \r\n" + e.localizedMessage)
                        builder.setPositiveButton("确定", null)
                        val dialog = builder.create()
                        dialog.window!!.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                        dialog.setCanceledOnTouchOutside(false)
                        dialog.show()
                        return
                    } catch (e2: Exception) {
                        LogUtil.log(e2, false)
                        return
                    } finally {
                        this@MainService.exit()
                    }
                }
                2 -> {
                    this@MainService.windowManager!!.addView(SongService.songView, SongService.songView!!.layoutParams)
                    return
                }
                3 -> {
                    this@MainService.windowManager!!.addView(SongService.songMenuView, SongService.songMenuView!!.layoutParams)
                    return
                }
                SHOW_BARRAGEVIEW -> {
                    this@MainService.windowManager!!.addView(BarrageService.barrageView, BarrageService.barrageView!!.layoutParams)
                    return
                }
                UPDATE_BARRAGEVIEW -> {
                    this@MainService.windowManager!!.updateViewLayout(BarrageService.barrageView, BarrageService.barrageView!!.layoutParams)
                    return
                }
                else -> {
                    return
                }
            }
        }
    }

    private fun initLog() {
        LogUtil.recordPath = EnvironmentUtil.getMainPath() + "/system/log/exception.thp"
        LogUtil.clearExceptionListeners() /* 防重复注册:static 列表跨多次 Service 启动累积 */
        LogUtil.registerExceptionListener(object : LogUtil.ExceptionListener {
            override fun occurException(e: Exception?) {
                val message = Message()
                message.obj = e
                message.what = 1
                this@MainService.handler.sendMessage(message)
            }
        })
    }

    private fun initService(mainService: MainService) {
        DesktopService.init(mainService)
        AnimationService.init(mainService)
        DialogService.init(mainService)
        SongService.loadSong(mainService)
    }

    override fun onCreate() {
        context = this
        windowManager = getApplicationContext().getSystemService(WindowManager::class.java)
        try {
            initLog()
        } catch (e1: Exception) {
            LogUtil.log(e1)
        }
        try {
            initService(this)
        } catch (e12: Exception) {
            LogUtil.log(e12)
        }
        if (PetService.pet != null) {
            pet = PetService.pet
            try {
                if (pet!!.getCurrentState() is DeadState) {
                    DialogService.confirm(
                        "TouhouPet",
                        "你的宠物已被四季找去喝茶...\r\n\r\n领回需要贿赂四季(宠物等级*10)个P点",
                        "领回", "重新来过",
                        object : DialogService.CallBack {
                            override fun onReturn(retVal: Boolean) {
                                LogUtil.log("click!")
                                if (retVal) {
                                    if (this@MainService.pet!!.getPower() >= this@MainService.pet!!.getLevel() * 10) {
                                        this@MainService.pet!!.changePower((-this@MainService.pet!!.getLevel()) * 10)
                                        this@MainService.pet!!.changeEnergy(100)
                                        this@MainService.pet!!.changeRepletionDegree(100)
                                        this@MainService.pet!!.changeDrinkDegree(100)
                                        this@MainService.pet!!.requestChangeState(StateService.ACTIVE!!, BasePet.MAX_LEVEL)
                                        return
                                    }
                                    DialogService.alert(
                                        "TouhouPet", "你没有足够的P点",
                                        object : DialogService.CallBack {
                                            override fun onReturn(retVal2: Boolean) {
                                                this@MainService.exitWithoutSave()
                                            }
                                        })
                                    return
                                }
                                SaveLoadUtil.clear("pet.thp")
                                SaveLoadUtil.clear("item.thp")
                                PetService.pet = null
                                this@MainService.exitWithoutSave()
                            }
                        })
                }
                ViewService.sliderView = SliderView(this)
                ViewService.sliderHandlerView = SliderHandlerView(this)
                ViewService.petView = PetView(this)
                Diag.log("petView created, animW=" + AnimationService.petWidth + " animH=" + AnimationService.petHeight)
                ViewService.statusView = StatusView(this)
                ViewService.petView!!.show()
                Diag.log("petView.show() called")
                notificationManager = getSystemService(NotificationManager::class.java)
                val intent = Intent().apply {
                    action = "thp"
                }
                /* 现代化:Notification.Builder + 渠道(API 26+ 必须);FLAG_IMMUTABLE(targetSdk 31+ 必须);
                 * startForeground 带 specialUse 类型(Android 14+ 必须)。 */
                /* 开发者面板入口:点击通知栏"状态"文字(按钮会把通知布局挤坏;
                 * 通知主体点击仍是 thp=隐藏/显示宠物,原交互保留) */
                val contentView = RemoteViews(packageName, R.layout.notification)
                if (BuildConfig.DEV_PANEL) {
                    contentView.setOnClickPendingIntent(
                        R.id.notification_tv_state,
                        PendingIntent.getBroadcast(
                            this, 1, Intent(this, DevReceiver::class.java),
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                    )
                }
                notification = Notification.Builder(this, PetApplication.CHANNEL_ID)
                    .setSmallIcon(R.drawable.satori_blink)
                    .setContentTitle("TouhouPet")
                    .setOngoing(true)
                    .setContent(contentView)
                    .setContentIntent(
                        PendingIntent.getBroadcast(
                            this, 0, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                    )
                    .build()
                startForeground(
                    NOTIFICATION_ID, notification!!,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                )
                currentTime = SystemClock.elapsedRealtime()
                lastTime = currentTime
                saveTime = SAVE_INTERVAL
                gameLoop = GameLoop(updateInterval) { time ->
                    val elapsed = time * timeMultiplier
                    this.pet!!.addLifeTime(elapsed * 1)
                    saveTime -= elapsed
                    if (saveTime <= 0) {
                        saveTime = SAVE_INTERVAL
                        save()
                    }
                }
                gameLoop!!.start()
                ListenerService.registerListener(object : OnPetPropertyChangeListener {
                    override fun onPetPropertyChange(petPropertyChangeEvent: PetPropertyChangeEvent) {
                        if (petPropertyChangeEvent.property != 3) {
                            updateNotification()
                        }
                    }
                })
                updateNotification()
                return
            } catch (e14: Exception) {
                LogUtil.log(e14)
                return
            }
        }
        LogUtil.log("cannot get pet")
        exit()
    }

    fun requestNewDialog() {
        handler.sendEmptyMessage(0)
    }

    fun showSongView() {
        handler.sendEmptyMessage(2)
    }

    fun showSongMenuView() {
        handler.sendEmptyMessage(3)
    }

    fun hideSongView() {
        windowManager!!.removeView(SongService.songView)
    }

    fun hideSongMenuView() {
        windowManager!!.removeView(SongService.songMenuView)
    }

    fun requestNewBarrageView() {
        handler.sendEmptyMessage(SHOW_BARRAGEVIEW)
    }

    fun updateBarrageView() {
        handler.sendEmptyMessage(UPDATE_BARRAGEVIEW)
    }

    fun removeBarrageView() {
        windowManager!!.removeView(BarrageService.barrageView)
    }

    private var exited = false

    fun exit() {
        if (exited) {
            return /* 幂等:异常链可能多次触发 exit */
        }
        exited = true
        SongService.exit()
        if (gameLoop != null) {
            gameLoop!!.stop()
        }
        if (pet != null) {
            if (pet!!.getCurrentState() != null) {
                pet!!.getCurrentState().onPause()
            }
            save()
        }
        releaseService()
        if (notificationManager != null) {
            notificationManager!!.cancel(NOTIFICATION_ID)
        }
        stopSelf()
    }

    fun exitWithoutSave() {
        if (exited) {
            return /* 幂等 */
        }
        exited = true
        if (gameLoop != null) {
            gameLoop!!.stop()
        }
        pet!!.getCurrentState().onPause()
        releaseService()
        notificationManager!!.cancel(NOTIFICATION_ID)
        stopSelf()
    }

    private fun releaseService() {
        DesktopService.release()
        AnimationService.release()
        BarrageService.release()
        DialogService.release()
        ItemService.release()
        ListenerService.release()
        LocationService.release()
        StateService.release()
        ViewService.release()
        PetService.pet = null
    }

    fun save() {
        try {
            pet!!.onSave()
            SaveLoadUtil.save(pet, "pet.thp")
            LogUtil.log("save success")
        } catch (e: Exception) {
            LogUtil.log("save fail")
            LogUtil.log(e, false)
        }
    }

    fun load() {
        try {
            pet = SaveLoadUtil.load(BasePet::class.java, "pet.thp") as BasePet?
        } catch (e: Exception) {
            Log.e("LOG", "load pet fail")
            LogUtil.log(e, false)
        }
        if (pet != null) {
            pet!!.onLoad()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        LogUtil.log("Service is destroy")
    }

    companion object {
        const val GAME_SPEED = 1
        private const val INTERRUPT = 1
        private const val NOTIFICATION_ID = 16
        private const val REQUEST_DIALOG = 0
        private const val SAVE_INTERVAL = 600000
        private const val SHOW_BARRAGEVIEW = 4
        private const val SHOW_SONGMENUVIEW = 3
        private const val SHOW_SONGVIEW = 2
        private const val UPDATE_BARRAGEVIEW = 5

        @JvmField
        var context: MainService? = null
        /* 时间加速倍率(DevPanel 测试用):GameLoop tick 的 elapsed 乘此值后再喂给 addLifeTime */
        @Volatile
        @JvmField
        var timeMultiplier = 1

        @JvmField
        var notification: Notification? = null

        @JvmField
        var notificationManager: NotificationManager? = null

        @Suppress("DEPRECATION")
        @JvmStatic
        fun updateNotification() {
            if (notification != null && PetService.pet != null) {
                notification!!.contentView!!.setTextViewText(R.id.notification_tv_state, "状态:" + PetService.pet!!.getCurrentState().getStateDoingDescription())
                notification!!.contentView!!.setTextViewText(R.id.notification_tv_rd, "饱食:" + PetService.pet!!.getRepletionDegree() + "/100")
                notification!!.contentView!!.setTextViewText(R.id.notification_tv_dd, "饮水:" + PetService.pet!!.getDrinkDegree() + "/100")
                notification!!.contentView!!.setTextViewText(R.id.notification_tv_en, "精力:" + PetService.pet!!.getEnergy() + "/100")
                notificationManager!!.notify(NOTIFICATION_ID, notification)
            }
        }
    }
}