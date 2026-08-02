package k.p.action.barrage

import k.p.domain.BasePet
import k.p.domain.Flag

class Youmu : BaseEnemy() {
    init {
        name = "魂魄 妖梦"
        strength = 37
        speed = 87
        magic = 34
    }

    override fun getWinMessage(): String = "获得:\r\n 点数 x 40 , P点 x 20"
    override fun getLoseMessage(): String = ""
    override fun getStartMessage(): String = "\"幽幽子大人的食物,由我来守护!\"\r\n\r\n需要等级 : 20"
    override fun canDone(pet: BasePet): Boolean = pet.level >= 20

    override fun onWin(pet: BasePet) {
        super.onWin(pet)
        pet.changePower(20)
        pet.changePoint(40)
        if (pet.getPetSetting("WinYoumu") == null) {
            pet.setAchievement(pet.achievement + 1)
            pet.setPetSetting("WinYoumu", Flag())
        }
    }

    override fun onLose(pet: BasePet) {
        super.onLose(pet)
    }

    override fun onCauseDamage(target: Shoujo, damage: Int) {
        damageTarget(damage, false)
        sendMessage(name.toString() + " 对 " + target.name + ",造成" + damage + "点伤害")
    }
}
