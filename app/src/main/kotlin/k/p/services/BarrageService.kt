package k.p.services

import android.app.AlertDialog
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.MotionEvent
import android.view.WindowManager
import k.p.action.barrage.BaseEnemy
import k.p.action.barrage.BaseShoujo
import k.p.action.barrage.Shoujo
import k.p.domain.BasePet
import k.p.main.MainService
import k.p.modern.Tasks
import java.util.ArrayList
import java.util.Random

/** 弹幕战斗窗口:原生 AlertDialog(TYPE_APPLICATION_OVERLAY)。
 *  现代化:原版白底自绘布局换为系统对话框, 跟随深浅色主题;
 *  窗口可拖动(按住对话框任意处移动), 拖动逻辑与桌宠本体一致。 */
class BarrageService private constructor() {
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            dialog?.setMessage(buildBattleText())
        }
    }
    private var dialog: AlertDialog? = null
    private var statusList: MutableList<String>? = null

    private fun newBarrage0(pet: BasePet, enemy: BaseEnemy) {
        if (statusList != null) {
            statusList!!.clear()
        } else {
            statusList = ArrayList()
        }
        player = BaseShoujo(pet.getStrength(), pet.getSpeed(), pet.getMagic())
        player!!.name = pet.getName()
        target = enemy
        player!!.init()
        target!!.init()
        /* AlertDialog 必须在主线程构建/显示; newBarrage 经 DialogService 回调触发(已在主线程) */
        mainHandler.post {
            dismiss()
            val builder = AlertDialog.Builder(MainService.context!!)
            builder.setTitle("弹幕对战")
            val dlg = builder.create()
            dlg.window!!.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            dlg.setCancelable(false)
            dlg.setCanceledOnTouchOutside(false)
            attachDrag(dlg)
            dialog = dlg
            dlg.setMessage(buildBattleText())
            dlg.show()
        }
        Tasks.loop(
            2000L, 100,
            { roundCount ->
                putString("回合" + roundCount + "-------")
                var keep = true
                if (player!!.speed > target!!.speed) {
                    if (!roundDamage(player!!, target!!, roundCount) || !roundDamage(target!!, player!!, roundCount)) {
                        keep = false
                    } else {
                        handler.sendEmptyMessage(0)
                    }
                } else {
                    if (!roundDamage(target!!, player!!, roundCount) || !roundDamage(player!!, target!!, roundCount)) {
                        keep = false
                    } else {
                        handler.sendEmptyMessage(0)
                    }
                }
                keep
            },
            Runnable {
                var winner: String
                if (player!!.currentHP <= 0) {
                    winner = target!!.name + " 获得了胜利!"
                } else {
                    winner = player!!.name + " 获得了胜利!"
                }
                putString(winner)
                handler.sendEmptyMessage(0)
                dismiss()
                if (player!!.currentHP <= 0) {
                    target!!.onLose(PetService.pet!!)
                } else {
                    target!!.onWin(PetService.pet!!)
                }
            }
        )
    }

    /* 拖动: 按下记录起点, 移动时按差值更新窗口 x/y */
    private fun attachDrag(dlg: AlertDialog) {
        val lp = dlg.window!!.attributes
        var downRawX = 0
        var downRawY = 0
        var downWinX = 0
        var downWinY = 0
        dlg.window!!.decorView.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    downRawX = event.rawX.toInt()
                    downRawY = event.rawY.toInt()
                    downWinX = lp.x
                    downWinY = lp.y
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    lp.x = downWinX + (event.rawX.toInt() - downRawX)
                    lp.y = downWinY + (event.rawY.toInt() - downRawY)
                    dlg.window!!.attributes = lp
                    true
                }
                else -> false
            }
        }
    }

    private fun buildBattleText(): String {
        return "敌方 : " + target!!.name + "   HP " + target!!.currentHP + "/" + target!!.maxHP +
            "\n--------------------------------------------\n" +
            getStringFromList(statusList!!) +
            "--------------------------------\n" +
            "我方 : " + player!!.name + "   HP " + player!!.currentHP + "/" + player!!.maxHP
    }

    private fun getStringFromList(list: List<String>): String {
        val sb = StringBuilder()
        for (str in list) {
            sb.append(str)
            sb.append("\r\n")
        }
        return sb.toString()
    }

    private fun roundDamage(src: Shoujo, target2: Shoujo, roundCount: Int): Boolean {
        val random = Random()
        src.onRoundStart(target2, roundCount)
        val damage: Int
        if (src.speed > 0) {
            damage = src.magic + random.nextInt(src.speed)
        } else if (src.speed < 0) {
            damage = src.magic - random.nextInt(-src.speed)
        } else {
            damage = src.magic
        }
        if (damage > 0) {
            target2.currentHP = target2.currentHP - damage
            putString(src.name + " 对 " + target2.name + " 造成了 " + damage + " 点伤害!")
            src.onCauseDamage(target2, damage)
            target2.onDamaged(src, damage)
            if (target2.currentHP <= 0) {
                target2.currentHP = 0
                return false
            }
        }
        src.onRoundEnd(target2, roundCount)
        if (target2.currentHP > 0) {
            return true
        }
        target2.currentHP = 0
        return false
    }

    companion object {
        @JvmField
        var player: BaseShoujo? = null
        @JvmField
        var target: BaseEnemy? = null

        private var instance = BarrageService()
        private val mainHandler = Handler(Looper.getMainLooper())

        @JvmStatic
        fun newBarrage(b: BasePet, enemy: BaseEnemy) {
            /* 关闭上一场残留窗口(若有): 重置 instance 前先 dismiss 旧实例的 dialog */
            val old = instance
            if (Looper.myLooper() == Looper.getMainLooper()) {
                old.dialog?.dismiss()
            } else {
                mainHandler.post { old.dialog?.dismiss() }
            }
            instance = BarrageService()
            instance.newBarrage0(b, enemy)
        }

        @JvmStatic
        fun putString(status: String?) {
            if (status != null && instance.statusList != null) {
                instance.statusList!!.add(status)
                if (instance.statusList!!.size > 15) {
                    instance.statusList!!.removeAt(0)
                }
            }
        }

        @JvmStatic
        fun dismiss() {
            /* 可能后台线程调用(战斗结束 Runnable); 主线程调用时直接执行避免嵌套 post 顺序错乱 */
            if (Looper.myLooper() == Looper.getMainLooper()) {
                instance.dialog?.dismiss()
                instance.dialog = null
            } else {
                mainHandler.post {
                    instance.dialog?.dismiss()
                    instance.dialog = null
                }
            }
        }

        @JvmStatic
        fun release() {
            dismiss()
            player = null
            target = null
            instance = BarrageService()
        }
    }
}