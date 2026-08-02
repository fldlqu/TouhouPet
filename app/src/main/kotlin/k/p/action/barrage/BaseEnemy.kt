package k.p.action.barrage

import k.p.domain.BasePet
import k.p.services.DialogService

abstract class BaseEnemy : BaseShoujo() {
    abstract fun canDone(basePet: BasePet): Boolean
    abstract fun getLoseMessage(): String
    abstract fun getStartMessage(): String
    abstract fun getWinMessage(): String

    open fun onWin(pet: BasePet) {
        DialogService.alert("胜利", "成功击败了" + name + "\r\n\r\n" + getWinMessage())
    }

    open fun onLose(pet: BasePet) {
        DialogService.alert("失败", "满身苍夷...")
    }
}
