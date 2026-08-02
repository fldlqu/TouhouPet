package k.p.listener

class PetPropertyChangeEvent(property: Int, changeValue: Int) {
    var property: Int = property
        private set
    var changeValue: Int = changeValue
        private set

    companion object {
        const val REPLETIONDEGREE = 0
        const val DRINKDEGREE = 1
        const val ENERGY = 2
        const val EXP = 3
    }
}
