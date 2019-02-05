package codes.spectrum.konveyor

/**
 * Main Konveyor class that includes all workflow of the konveyor
 */
class SubKonveyorWrapper<T, S>(
    private val subKonveyor: Konveyor<S> = Konveyor(),
    private val splitter: suspend T.() -> Iterable<S> = { listOf<S>() },
    private val joiner: suspend T.(joining: S) -> Unit = { }
): IKonveyorHandler<T> {

    override fun match(context: T): Boolean = true

    override suspend fun exec(context: T) {
        context
            .splitter()
            .forEach {
                subKonveyor.exec(it)
                context.joiner(it)
            }
    }
}

