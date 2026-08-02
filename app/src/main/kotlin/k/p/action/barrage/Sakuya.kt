package k.p.action.barrage

import k.p.domain.BasePet
import k.p.domain.Flag
import java.util.Random

class Sakuya : BaseEnemy() {
    init {
        name = "十六夜 咲夜"
        strength = 76
        speed = 105
        magic = 85
    }

    override fun getWinMessage(): String = "获得:\r\n 点数 x 30 , P点 x 30"
    override fun getLoseMessage(): String = ""
    override fun getStartMessage(): String = "\"经常来偷红茶和蛋糕的小鬼!\"\r\n\r\n需要等级 : 20"
    override fun canDone(pet: BasePet): Boolean = pet.level >= 20

    override fun onWin(pet: BasePet) {
        super.onWin(pet)
        pet.changePower(30)
        pet.changePoint(30)
        if (pet.getPetSetting("WinSakuya") == null) {
            pet.setAchievement(pet.achievement + 1)
            pet.setPetSetting("WinSakuya", Flag())
        }
    }

    override fun onLose(pet: BasePet) {
        super.onLose(pet)
    }

    override fun onCauseDamage(target: Shoujo, damage: Int) {
        val r = Random().nextInt(100)
        if (r < 30) {
            damageTarget(damage + damage / 2, false)
            sendMessage("「咲夜的世界」")
            sendMessage(name.toString() + " 对 " + target.name + ",造成" + (damage + damage / 2) + "点伤害")
        }
    }
}
