package k.p.domain

class ReturnStatus<T>(success: Boolean, status: T?) {
    var success: Boolean = success
        private set
    var status: T? = status
        private set

    constructor(success: Boolean) : this(success, null)

    companion object {
        @JvmField
        val TRUE: ReturnStatus<Void> = ReturnStatus(true)
        @JvmField
        val FALSE: ReturnStatus<Void> = ReturnStatus(false)
    }
}
