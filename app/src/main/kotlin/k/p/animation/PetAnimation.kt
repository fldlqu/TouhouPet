package k.p.animation

class PetAnimation {
    private var currentFrame = 0
    private var currentLoopIndex = 0
    private val list = mutableListOf<PetAnimationInfo>()
    var loop = 0
    var name: String? = null
    var type: Array<String>? = null

    fun reset() {
        currentFrame = 0
        currentLoopIndex = loop
    }

    fun nextFrame(): PetAnimationInfo? {
        if (currentFrame < list.size) {
            val info = list[currentFrame]
            currentFrame++
            return info
        }
        if (currentLoopIndex <= 0) {
            return null
        }
        currentLoopIndex--
        currentFrame = 0
        return list[currentFrame]
    }

    fun addActionInfo(info: PetAnimationInfo) {
        list.add(info)
    }

    fun getList(): MutableList<PetAnimationInfo> = list

    fun getCurrentLoopIndex(): Int = currentLoopIndex

    fun setCurrentLoopIndex(currentLoopIndex: Int) {
        this.currentLoopIndex = currentLoopIndex
    }

    fun getCurrentFrame(): Int = currentFrame

    fun setCurrentFrame(currentFrame: Int) {
        this.currentFrame = currentFrame
    }
}
