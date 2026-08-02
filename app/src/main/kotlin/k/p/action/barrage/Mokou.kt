package k.p.action.barrage

import k.p.domain.BasePet
import k.p.domain.Flag
import java.util.Random

class Mokou : BaseEnemy() {
    init {
        name = "藤原 妹红"
        strength = 455
        speed = 435
        magic = 442
    }

    override fun getWinMessage(): String = "获得:\r\n 点数 x 80 , P点 x 80"
    override fun getLoseMessage(): String = ""
    override fun getStartMessage(): String = "\"无聊啊无聊啊...\"\r\n\r\n需要等级 : 40"
    override fun canDone(pet: BasePet): Boolean = pet.level >= 40

    override fun onWin(pet: BasePet) {
        super.onWin(pet)
        pet.changePower(80)
        pet.changePoint(80)
        if (pet.getPetSetting("WinMokou") == null) {
            pet.setAchievement(pet.achievement + 2)
            pet.setPetSetting("WinMokou", Flag())
        }
    }

    override fun onLose(pet: BasePet) {
        super.onLose(pet)
    }

    override fun onDamaged(src: Shoujo, damage: Int) {
        if (currentHP <= 0 && Random().nextInt(100) < 70) {
            sendMessage("「不死鸟重生」")
            currentHP = maxHP
        }
    }

    override fun onRoundStart(target: Shoujo, roundCount: Int) {
        val random = Random()
        if (random.nextInt(100) < 30) {
            val damage = random.nextInt(BasePet.MAX_LEVEL) + 1
            damageTarget(damage)
            sendMessage("「不朽的弹幕」")
            sendMessage(name.toString() + " 对 " + target.name + "造成" + damage + "点伤害")
        }
    }
}
