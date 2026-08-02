package k.p.action.barrage

import k.p.domain.BasePet
import k.p.domain.Flag
import java.util.Random

class ACirno : BaseEnemy() {
    init {
        name = "Advent琪露诺"
        strength = BasePet.MAX_LEVEL
        speed = BasePet.MAX_LEVEL
        magic = BasePet.MAX_LEVEL
    }

    override fun getWinMessage(): String = "获得经验999点"
    override fun getLoseMessage(): String = ""
    override fun getStartMessage(): String = "\"哦?又要来玩弹幕吗?\r\n这次可不会手下留情了\""
    override fun canDone(pet: BasePet): Boolean = true

    override fun onWin(pet: BasePet) {
        super.onWin(pet)
        pet.addExp(BasePet.MAX_LEVEL)
        if (pet.getPetSetting("WinACirno") == null) {
            pet.setAchievement(pet.achievement + 5)
            pet.setPetSetting("WinACirno", Flag())
        }
    }

    override fun onLose(pet: BasePet) {
        super.onLose(pet)
    }

    override fun onRoundStart(target: Shoujo, roundCount: Int) {
        val random = Random()
        val decreaseSpeed = (target.speed / 9) + 99
        target.speed -= decreaseSpeed
        sendMessage(target.name.toString() + "受到寒气影响,速度降低了" + decreaseSpeed + "点")
        if (target.speed < 99) {
            damageTarget(BasePet.MAX_LEVEL)
            sendMessage("「冰华⑨咲」")
            sendMessage(name.toString() + " 对 " + target.name + "造成999点伤害")
        }
        if (random.nextInt(100) < 30) {
            target.speed -= 999
            sendMessage("冻符「负K」")
            sendMessage(target.name.toString() + "的速度降低了999点")
        }
    }
}
