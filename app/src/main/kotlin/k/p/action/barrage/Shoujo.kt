package k.p.action.barrage

interface Shoujo {
    var currentHP: Int
    var magic: Int
    val maxHP: Int
    val name: String?
    var speed: Int
    var strength: Int
    fun init()
    fun onCauseDamage(shoujo: Shoujo, i: Int)
    fun onDamaged(shoujo: Shoujo, i: Int)
    fun onRoundEnd(shoujo: Shoujo, i: Int)
    fun onRoundStart(shoujo: Shoujo, i: Int)
    fun sendMessage(str: String)
}
