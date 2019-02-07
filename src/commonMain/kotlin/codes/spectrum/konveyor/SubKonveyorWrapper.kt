package codes.spectrum.konveyor

/**
 * Main Konveyor class that includes all workflow of the konveyor
 */
class SubKonveyorWrapper<T, S>(
    private val subKonveyor: Konveyor<S> = Konveyor(),
    private val splitter: SubKonveyorSplitterType<T, S> = { sequence { } },
    private val joiner: SubKonveyorJoinerType<T, S> = { _: S, _: IKonveyorEnvironment -> }
): IKonveyorHandler<T> {

    override fun match(context: T, env: IKonveyorEnvironment): Boolean = true

    override suspend fun exec(context: T, env: IKonveyorEnvironment) {
        fun T.getKonveyorEnv() = env
        context
            .splitter(env)
            .forEach {
                subKonveyor.exec(it, env)
                context.joiner(it, env)
            }
    }
}

