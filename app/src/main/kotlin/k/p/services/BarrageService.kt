package k.p.services

import android.os.Handler
import android.os.Message
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import k.p.action.barrage.BaseEnemy
import k.p.action.barrage.BaseShoujo
import k.p.action.barrage.Shoujo
import k.p.domain.BasePet
import k.p.main.MainService
import k.p.main.R
import k.p.modern.Tasks
import java.util.ArrayList
import java.util.Random

class BarrageService private constructor() {
    private var battleStatus: TextView? = null
    private var enemyHP: TextView? = null
    private var enemyName: TextView? = null
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            this@BarrageService.enemyHP!!.text = "" + target!!.currentHP + "/" + target!!.maxHP
            this@BarrageService.playerHP!!.text = "" + player!!.currentHP + "/" + player!!.maxHP
            battleStatus!!.setText(getStringFromList(this@BarrageService.statusList!!))
            MainService.context!!.updateBarrageView()
        }
    }
    private var playerHP: TextView? = null
    private var playerName: TextView? = null
    private var statusList: MutableList<String>? = null

    private fun newBarrage0(pet: BasePet, enemy: BaseEnemy) {
        if (statusList != null) {
            statusList!!.clear()
        } else {
            statusList = ArrayList()
        }
        barrageView = View.inflate(MainService.context, R.layout.barrage, null)
        enemyName = barrageView!!.findViewById(R.id.barrage_enemyname)
        enemyHP = barrageView!!.findViewById(R.id.barrage_enemyhp)
        battleStatus = barrageView!!.findViewById(R.id.barrage_battlestatus)
        playerName = barrageView!!.findViewById(R.id.barrage_playername)
        playerHP = barrageView!!.findViewById(R.id.barrage_playerhp)
        val params = WindowManager.LayoutParams()
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        params.flags = 520
        params.gravity = 17
        params.width = 600
        params.height = -2
        params.format = 1
        barrageView!!.layoutParams = params
        player = BaseShoujo(pet.getStrength(), pet.getSpeed(), pet.getMagic())
        player!!.name = pet.getName()
        target = enemy
        player!!.init()
        target!!.init()
        enemyName!!.setText(target!!.name)
        playerName!!.setText(player!!.name)
        enemyHP!!.setText("" + target!!.currentHP + "/" + target!!.maxHP)
        playerHP!!.setText("" + player!!.currentHP + "/" + player!!.maxHP)
        MainService.context!!.requestNewBarrageView()
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
                MainService.context!!.removeBarrageView()
                if (player!!.currentHP <= 0) {
                    target!!.onLose(PetService.pet!!)
                } else {
                    target!!.onWin(PetService.pet!!)
                }
            }
        )
    }

    companion object {
        @JvmField
        var barrageView: View? = null
        @JvmField
        var player: BaseShoujo? = null
        @JvmField
        var target: BaseEnemy? = null

        private var instance = BarrageService()

        @JvmStatic
        fun newBarrage(b: BasePet, enemy: BaseEnemy) {
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

        @JvmStatic
        fun release() {
            barrageView = null
            player = null
            target = null
            instance = BarrageService()
        }
    }
}