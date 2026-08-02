package k.p.action.barrage

import k.p.domain.BasePet
import k.p.domain.Flag
import java.util.Random

class Aya : BaseEnemy() {
    init {
        name = "射命丸 文"
        strength = 285
        speed = 775
        magic = 274
    }

    override fun getWinMessage(): String = "获得:\r\n 点数 x 80 , P点 x 80"
    override fun getLoseMessage(): String = ""
    override fun getStartMessage(): String = "\"来吧,我会放水的.认真的打过来吧!\"\r\n\r\n需要等级 : 30"
    override fun canDone(pet: BasePet): Boolean = pet.level >= 30

    override fun onWin(pet: BasePet) {
        super.onWin(pet)
        pet.changePower(80)
        pet.changePoint(80)
        if (pet.getPetSetting("WinAya") == null) {
            pet.setAchievement(pet.achievement + 2)
            pet.setPetSetting("WinAya", Flag())
        }
    }

    override fun onLose(pet: BasePet) {
        super.onLose(pet)
    }

    override fun onRoundStart(target: Shoujo, roundCount: Int) {
        val random = Random()
        if (random.nextInt(100) < 40) {
            sendMessage("「无双风神」")
            val k2 = (speed.toDouble() * 0.3).toInt()
            speed += k2
            sendMessage(name.toString() + "的速度提升了" + k2 + "点")
        }
        if (random.nextInt(100) < 30) {
            sendMessage("「幻想风靡」")
            val damage = speed
            damageTarget(damage)
            sendMessage(name.toString() + " 对 " + target.name + ",造成" + damage + "点伤害")
        }
    }
}
