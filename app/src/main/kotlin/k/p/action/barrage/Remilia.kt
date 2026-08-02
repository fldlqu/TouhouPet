package k.p.action.barrage

import k.p.domain.BasePet
import k.p.domain.Flag
import java.util.Random

class Remilia : BaseEnemy() {
    init {
        name = "蕾米莉亚 斯卡雷特"
        strength = 227
        speed = 213
        magic = 239
    }

    override fun getWinMessage(): String = "获得:\r\n 点数 x 60 , P点 x 60"
    override fun getLoseMessage(): String = ""
    override fun getStartMessage(): String = "\"阿类,地灵殿之主?\"\r\n\r\n需要等级 : 30"
    override fun canDone(pet: BasePet): Boolean = pet.level >= 30

    override fun onWin(pet: BasePet) {
        super.onWin(pet)
        pet.changePower(60)
        pet.changePoint(60)
        if (pet.getPetSetting("WinRemilia") == null) {
            pet.setAchievement(pet.achievement + 1)
            pet.setPetSetting("WinRemilia", Flag())
        }
    }

    override fun onLose(pet: BasePet) {
        super.onLose(pet)
    }

    override fun onCauseDamage(target: Shoujo, damage: Int) {
        val maxHP = this.maxHP
        val rHP = damage / 3
        currentHP += rHP
        if (currentHP > maxHP) {
            currentHP = maxHP
        }
        sendMessage(name.toString() + " 吸取生命值 " + rHP + " 点")
    }

    override fun onRoundStart(target: Shoujo, roundCount: Int) {
        val random = Random()
        val r = random.nextInt(100)
        if (r < 20) {
            val damage = random.nextInt(BasePet.MAX_LEVEL) + 1
            damageTarget(damage)
            sendMessage("神枪「冈格尼尔」")
            sendMessage(name.toString() + " 对 " + target.name + "造成" + damage + "点伤害")
        }
    }

    override fun onDamaged(src: Shoujo, damage: Int) {
        val random = Random()
        val r = random.nextInt(100)
        if (r < 30) {
            sendMessage("「抱头蹲防」")
            currentHP += damage
            if (currentHP > maxHP) {
                currentHP = maxHP
            }
        }
    }
}
