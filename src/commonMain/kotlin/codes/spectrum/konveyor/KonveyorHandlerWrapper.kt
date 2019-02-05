package codes.spectrum.konveyor

class KonveyorHandlerWrapper<T>(
    private val matcher: T.() -> Boolean = { true },
    private val executor: suspend T.() -> Unit = {}
): IKonveyorHandler<T> {

    override fun match(context: T): Boolean = context.matcher()
    override suspend fun exec(context: T) = context.executor()
}
