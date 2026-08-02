package k.p.action.barrage

import k.p.domain.BasePet
import k.p.domain.Flag
import k.p.item.food.Mushroom
import k.p.services.ItemService
import java.util.Random

class Marisa : BaseEnemy() {
    init {
        name = "魔理沙"
        strength = 24
        speed = 47
        magic = 68
    }

    override fun getWinMessage(): String = "获得:\r\n 点数 x 20 , P点 x 20 , 蘑菇 x 2"
    override fun getLoseMessage(): String = "怎么有这么多台词要写ZE"
    override fun getStartMessage(): String = "怎么有这么多台词要写ZE\r\n\r\n需要等级 : 20"
    override fun canDone(pet: BasePet): Boolean = pet.level >= 20

    override fun onWin(pet: BasePet) {
        super.onWin(pet)
        pet.changePower(20)
        pet.changePoint(20)
        ItemService.addItem(Mushroom())
        ItemService.addItem(Mushroom())
        if (pet.getPetSetting("WinMarisa") == null) {
            pet.setAchievement(pet.achievement + 1)
            pet.setPetSetting("WinMarisa", Flag())
        }
    }

    override fun onLose(pet: BasePet) {
        super.onLose(pet)
    }

    override fun onCauseDamage(target: Shoujo, damage: Int) {
        if (Random().nextInt(100) < 20) {
            damageTarget(120, false)
            sendMessage("魔理沙发动魔炮追击,造成120点伤害")
        }
    }
}
