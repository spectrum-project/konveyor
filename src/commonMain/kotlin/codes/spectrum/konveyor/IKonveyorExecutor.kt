package codes.spectrum.konveyor

interface IKonveyorExecutor<T> {
    suspend fun exec(context: T, env: IKonveyorEnvironment = DefaultKonveyorEnvironment)
}
