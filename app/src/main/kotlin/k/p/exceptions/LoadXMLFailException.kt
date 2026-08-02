package k.p.exceptions

class LoadXMLFailException(lineNumber: Int, override val message: String?) : Exception() {
    var lineNumber = lineNumber
        private set

    companion object {
        private const val serialVersionUID = 4383384909241379331L
    }
}
