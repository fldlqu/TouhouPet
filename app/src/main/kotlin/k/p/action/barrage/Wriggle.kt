package k.p.action.barrage

import k.p.domain.BasePet

class Wriggle : BaseEnemy() {
    init {
        name = "莉格露 奈特巴格"
        strength = 13
        speed = 15
        magic = 12
    }

    override fun getWinMessage(): String = "获得:\r\n 点数 x 10 , P点 x 10"
    override fun getLoseMessage(): String = ""
    override fun getStartMessage(): String = "\"~!@#$%^&*\""
    override fun canDone(pet: BasePet): Boolean = true

    override fun onWin(pet: BasePet) {
        super.onWin(pet)
        pet.changePower(10)
        pet.changePoint(10)
    }

    override fun onLose(pet: BasePet) {
        super.onLose(pet)
    }

    override fun onRoundStart(target: Shoujo, roundCount: Int) {
        if (roundCount == 1) {
            damageTarget(77)
            sendMessage("虫子飞起一脚")
            sendMessage(name.toString() + " 对 " + target.name + "造成77点伤害")
        }
    }
}
