package k.p.action.barrage

import k.p.domain.BasePet

class Cirno : BaseEnemy() {
    init {
        name = "琪露诺"
        strength = 9
        speed = 9
        magic = 9
    }

    override fun getWinMessage(): String = "获得经验9点"
    override fun getLoseMessage(): String = ""
    override fun getStartMessage(): String = "\"俺最强!\""
    override fun canDone(pet: BasePet): Boolean = true

    override fun onWin(pet: BasePet) {
        super.onWin(pet)
        pet.addExp(9)
        val count = pet.getPetSetting("BeatCirno") as? Int ?: 0
        pet.setPetSetting("BeatCirno", count + 1)
    }

    override fun onLose(pet: BasePet) {
        super.onLose(pet)
    }
}
