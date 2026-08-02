package k.p.action.barrage

import k.p.domain.BasePet
import k.p.domain.Flag
import java.util.Random

class Reisen : BaseEnemy() {
    init {
        name = "铃仙 优昙华院 因幡"
        strength = 165
        speed = 157
        magic = 172
    }

    override fun getWinMessage(): String = "获得:\r\n 点数 x 40 , P点 x 40"
    override fun getLoseMessage(): String = ""
    override fun getStartMessage(): String = "\"台词待补充(懒)\"\r\n\r\n需要等级 : 20"
    override fun canDone(pet: BasePet): Boolean = pet.level >= 20

    override fun onWin(pet: BasePet) {
        super.onWin(pet)
        pet.changePower(40)
        pet.changePoint(40)
        if (pet.getPetSetting("WinReisen") == null) {
            pet.setAchievement(pet.achievement + 1)
            pet.setPetSetting("WinReisen", Flag())
        }
    }

    override fun onLose(pet: BasePet) {
        super.onLose(pet)
    }

    override fun onRoundStart(target: Shoujo, roundCount: Int) {
        if (Random().nextInt(100) < 50) {
            val d = (target.magic.toDouble() * 0.1).toInt()
            target.magic -= d
            sendMessage("狂符「幻视调律」")
            sendMessage(target.name.toString() + "的灵力下降了" + d + "点")
        }
    }
}
