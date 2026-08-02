package k.p.action.barrage

import k.p.domain.BasePet
import k.p.domain.Flag
import java.util.Random

class Flandre : BaseEnemy() {
    init {
        name = "芙兰朵露 斯卡雷特"
        strength = 495
        speed = 367
        magic = 386
    }

    override fun getWinMessage(): String = "获得:\r\n 点数 x 122 , P点 x 122"
    override fun getLoseMessage(): String = ""
    override fun getStartMessage(): String = "\"玩坏你哦!\"\r\n\r\n需要等级 : 40"
    override fun canDone(pet: BasePet): Boolean = pet.level >= 40

    override fun onWin(pet: BasePet) {
        super.onWin(pet)
        pet.changePower(122)
        pet.changePoint(122)
        if (pet.getPetSetting("WinFlandre") == null) {
            pet.setAchievement(pet.achievement + 2)
            pet.setPetSetting("WinFlandre", Flag())
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
        if (roundCount >= 20) {
            val damage = random.nextInt(5555) + 1
            damageTarget(damage)
            sendMessage("秘弹「接下来谁都不剩了喔?」")
            sendMessage(name.toString() + " 对 " + target.name + "造成" + damage + "点伤害")
            return
        }
        val r = random.nextInt(100)
        if (r < 20) {
            val damage2 = random.nextInt(1999) + 1
            damageTarget(damage2)
            sendMessage("禁忌「莱瓦汀」")
            sendMessage(name.toString() + " 对 " + target.name + "造成" + damage2 + "点伤害")
            return
        }
        if (r < 40) {
            val increaseSpeed = random.nextInt(500)
            sendMessage("禁忌「四重存在」")
            speed += increaseSpeed
            sendMessage(name.toString() + " 的速度提升了 " + increaseSpeed + " 点")
        }
    }
}
