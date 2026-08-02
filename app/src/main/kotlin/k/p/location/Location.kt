package k.p.location

interface Location {
    fun getDescription(): String
    fun getName(): String
    fun getNearbyList(): List<Location>
    fun init()
    fun isVisible(): Boolean
    fun onEnter()
    fun onFinishSearch()
    fun onLeave()
    fun onStartSearch()
    fun setVisible(z: Boolean)
}
