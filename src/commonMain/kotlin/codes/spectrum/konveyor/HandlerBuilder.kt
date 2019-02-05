package codes.spectrum.konveyor

/**
 * The builder which builds Handler object
 *
 */
@KonveyorTagMarker
class HandlerBuilder<T> {

    private var matcher: T.() -> Boolean = { true }
    private var executor: suspend T.() -> Unit = { }

    /**
     * With this methos one can set the lambda for matcher [[IKonveyorHandler.match]] to the handler
     */
    fun on(block: T.() -> Boolean) {
        matcher = block
    }

    /**
     * With this methos one can set the lambda for executor [[IKonveyorHandler.exec]] to the handler
     */
    fun exec(block: suspend T.() -> Unit) {
        executor = block
    }

    /**
     * Builds the [[IKonveyorHandler]] implementation
     */
    fun build() = KonveyorHandlerWrapper<T>(
        matcher = matcher,
        executor = executor
    )
}
