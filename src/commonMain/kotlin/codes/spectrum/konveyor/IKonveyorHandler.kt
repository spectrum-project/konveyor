package codes.spectrum.konveyor

interface IKonveyorHandler<T>: IKonveyorExecutor<T> {
    fun match(context: T, env: IKonveyorEnvironment = DefaultKonveyorEnvironment): Boolean
}
