package codes.spectrum.konveyor

/**
 * Main Konveyor class that includes all workflow of the konveyor
 */
open class Konveyor<T>(
    private val handlers: List<IKonveyorHandler<T>> = listOf()
): IKonveyorHandler<T> {
    override fun match(context: T, env: IKonveyorEnvironment): Boolean = true

    override suspend fun exec(context: T, env: IKonveyorEnvironment) {
        handlers.forEach {
            if (it.match(context, env)) it.exec(context, env)
        }
    }
}

