package k.p.action.barrage

import k.p.services.BarrageService

open class BaseShoujo : Shoujo {
    override var currentHP = 0
    override var magic = 0
    override var maxHP = 0
    override var name: String? = null
    override var speed = 0
    override var strength = 0

    constructor()

    constructor(strength: Int, speed: Int, magic: Int) {
        this.strength = strength
        this.speed = speed
        this.magic = magic
    }

    override fun init() {
        maxHP = strength * 20
        currentHP = maxHP
    }

    override fun onCauseDamage(target: Shoujo, damage: Int) {
    }

    override fun onDamaged(src: Shoujo, damage: Int) {
    }

    override fun onRoundStart(target: Shoujo, roundCount: Int) {
    }

    override fun onRoundEnd(target: Shoujo, roundCount: Int) {
    }

    override fun sendMessage(message: String) {
        BarrageService.putString(message)
    }

    fun damageTarget(damage: Int) {
        damageTarget(damage, true)
    }

    fun damageTarget(damage: Int, triggerEvent: Boolean) {
        if (this === BarrageService.player) {
            BarrageService.target!!.currentHP -= damage
            if (triggerEvent) {
                BarrageService.player!!.onCauseDamage(BarrageService.target!!, damage)
                if (BarrageService.target!!.currentHP > 0) {
                    BarrageService.target!!.onDamaged(BarrageService.player!!, damage)
                    return
                }
                return
            }
            return
        }
        BarrageService.player!!.currentHP -= damage
        if (triggerEvent) {
            BarrageService.target!!.onCauseDamage(BarrageService.player!!, damage)
            if (BarrageService.player!!.currentHP > 0) {
                BarrageService.player!!.onDamaged(BarrageService.target!!, damage)
            }
        }
    }
}
