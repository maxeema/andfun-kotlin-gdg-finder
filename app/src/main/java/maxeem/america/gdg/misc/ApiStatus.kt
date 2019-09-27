package maxeem.america.gdg.misc

sealed class ApiStatus {

    object Loading : ApiStatus()
    object Success : ApiStatus()

    open class Error protected constructor(val err: Throwable) : ApiStatus() {
        companion object {
            fun of (err: Throwable) = when (err.javaClass) {
                in arrayOf<Class<out Throwable>>(
                            retrofit2.HttpException::class.java,
                            java.net.UnknownHostException::class.java,
                            java.net.ConnectException::class.java,
                            java.net.SocketTimeoutException::class.java)
                     -> ConnectionError(err)
                else -> Error(err)
            }}
    }
    class ConnectionError(err: Throwable) : Error(err)

}