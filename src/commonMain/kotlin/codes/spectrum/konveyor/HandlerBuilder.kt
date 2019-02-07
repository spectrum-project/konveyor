package codes.spectrum.konveyor

/**
 * The builder which builds Handler object
 *
 */
@KonveyorTagMarker
open class HandlerBuilder<T> {

    private var matcher: KonveyorMatcherType<T> = { true }
    private var executor: KonveyorExecutorType<T> = { }

    /**
     * With this methos one can set the lambda for matcher [[IKonveyorHandler.match]] to the handler
     */
    fun on(block: KonveyorMatcherShortType<T>) {
        onEnv { block() }
    }

    /**
     * With this methos one can set the lambda for matcher [[IKonveyorHandler.match]] having access to
     * [[IKonveyorEnvironment]] through lambda parameter to the handler
     */
    fun onEnv(block: KonveyorMatcherType<T>) {
        matcher = block
    }

    /**
     * With this methods one can set the lambda for executor [[IKonveyorHandler.exec]] to the handler
     */
    open fun exec(block: KonveyorExecutorShortType<T>) {
        execEnv {
            block()
        }
    }

    /**
     * With this methods one can set the lambda for executor [[IKonveyorHandler.exec]] having access to
     * [[IKonveyorEnvironment]] through lambda parameter to the handler
     */
    open fun execEnv(block: KonveyorExecutorType<T>) {
        executor = block
    }

    /**
     * Builds the [[IKonveyorHandler]] implementation
     */
    open fun build(): IKonveyorHandler<T> = KonveyorHandlerWrapper<T>(
        matcher = matcher,
        executor = executor
    )
}
