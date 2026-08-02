package k.p.action.barrage

import k.p.domain.BasePet
import k.p.domain.Flag
import java.util.Random

class Patchouli : BaseEnemy() {
    init {
        name = "帕秋莉 诺蕾姬"
        strength = 79
        speed = 157
        magic = 267
    }

    override fun getWinMessage(): String = "获得:\r\n 点数 x 30 , P点 x 40"
    override fun getLoseMessage(): String = ""
    override fun getStartMessage(): String = "\"咳...咳.!\"\r\n\r\n需要等级 : 20"
    override fun canDone(pet: BasePet): Boolean = pet.level >= 20

    override fun onWin(pet: BasePet) {
        super.onWin(pet)
        pet.changePower(40)
        pet.changePoint(30)
        if (pet.getPetSetting("WinPatchouli") == null) {
            pet.setAchievement(pet.achievement + 1)
            pet.setPetSetting("WinPatchouli", Flag())
        }
    }

    override fun onLose(pet: BasePet) {
        super.onLose(pet)
    }

    override fun onRoundStart(target: Shoujo, roundCount: Int) {
        val r = Random().nextInt(100)
        if (r < 20) {
            val restoreHP = (maxHP - currentHP) / 3
            sendMessage("水符「水精公主」")
            sendMessage(name.toString() + " 回复了 " + restoreHP + " 点生命")
            currentHP += restoreHP
            return
        }
        if (r < 40) {
            val damage = r * 20
            damageTarget(damage)
            sendMessage("火符「火神闪光」")
            sendMessage(name.toString() + " 对 " + target.name + "造成" + damage + "点伤害")
            return
        }
        if (r < 60) {
            target.magic -= 50
            sendMessage("金符「金属疲劳」")
            sendMessage(target.name.toString() + "的灵力下降了50点")
        }
    }
}
