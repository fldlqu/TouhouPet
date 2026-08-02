package k.p.action.barrage

import k.p.domain.BasePet
import k.p.domain.Flag

class Yuugi : BaseEnemy() {
    init {
        name = "星熊 勇仪"
        strength = 60
        speed = 22
        magic = 46
    }

    override fun getWinMessage(): String = "获得:\r\n 点数 x 20 , P点 x 40"
    override fun getLoseMessage(): String = ""
    override fun getStartMessage(): String = "\"是来喝酒的吗?\"\r\n\r\n需要等级 : 20"
    override fun canDone(pet: BasePet): Boolean = pet.level >= 20

    override fun onWin(pet: BasePet) {
        super.onWin(pet)
        pet.changePower(40)
        pet.changePoint(20)
        if (pet.getPetSetting("WinYuugi") == null) {
            pet.setAchievement(pet.achievement + 1)
            pet.setPetSetting("WinYuugi", Flag())
        }
    }

    override fun onLose(pet: BasePet) {
        super.onLose(pet)
    }

    override fun onRoundStart(target: Shoujo, roundCount: Int) {
        if (roundCount == 3) {
            damageTarget(333)
            sendMessage("四天王奥义「三步必杀」")
            sendMessage(name.toString() + " 对 " + target.name + "造成333点伤害")
        }
    }
}
