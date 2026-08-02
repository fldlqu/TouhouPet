package k.p.action.barrage

import k.p.domain.BasePet
import java.util.Random

class ExCirno : BaseEnemy() {
    init {
        name = "Ex琪露诺"
        strength = 99
        speed = 99
        magic = 99
    }

    override fun getWinMessage(): String = "获得经验99点"
    override fun getLoseMessage(): String = ""
    override fun getStartMessage(): String = "\"干得不错嘛!\""
    override fun canDone(pet: BasePet): Boolean = true

    override fun onWin(pet: BasePet) {
        super.onWin(pet)
        pet.addExp(99)
        if (pet.getPetSetting("BeatEXCirno") == null) {
            pet.setAchievement(pet.achievement + 1)
        }
        val count = pet.getPetSetting("BeatEXCirno") as? Int ?: 0
        pet.setPetSetting("BeatEXCirno", count + 1)
    }

    override fun onLose(pet: BasePet) {
        super.onLose(pet)
    }

    override fun onRoundStart(target: Shoujo, roundCount: Int) {
        val random = Random()
        val decreaseSpeed = (target.speed / 99) + 9
        if (decreaseSpeed > 0) {
            target.speed -= decreaseSpeed
            sendMessage(target.name.toString() + "受到寒气影响,速度降低了" + decreaseSpeed + "点")
        }
        if (target.speed < 99) {
            damageTarget(99)
            sendMessage("「冰华⑨咲」")
            sendMessage(name.toString() + " 对 " + target.name + "造成99点伤害")
        }
        val r = random.nextInt(100)
        if (r < 30) {
            target.speed -= 99
            sendMessage("冻符「负K」")
            sendMessage(target.name.toString() + "的速度降低了99点")
        }
    }
}
