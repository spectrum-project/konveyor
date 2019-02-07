package codes.spectrum.konveyor

class KonveyorHandlerWrapper<T>(
    private val matcher: KonveyorMatcherType<T> = { true },
    private val executor: KonveyorExecutorType<T>
): IKonveyorHandler<T> {

    override fun match(context: T, env: IKonveyorEnvironment): Boolean = context.matcher(env)
    override suspend fun exec(context: T, env: IKonveyorEnvironment) = context.executor(env)
}
