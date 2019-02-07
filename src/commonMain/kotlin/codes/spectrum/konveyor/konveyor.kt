package codes.spectrum.konveyor

/**
 * Global method for creation of a conveyor
 */
fun <T> konveyor(body: KonveyorBuilder<T>.() -> Unit ): Konveyor<T> {
    val builder = KonveyorBuilder<T>()
    builder.body()
    return builder.build()
}

typealias KonveyorExecutorType<T> = suspend T.(IKonveyorEnvironment) -> Unit
typealias KonveyorMatcherType<T> = T.(IKonveyorEnvironment) -> Boolean
typealias KonveyorExecutorShortType<T> = suspend T.() -> Unit
typealias KonveyorMatcherShortType<T> = T.() -> Boolean

typealias SubKonveyorJoinerType<T, S> = suspend T.(joining: S, env: IKonveyorEnvironment) -> Unit
typealias SubKonveyorSplitterType<T, S> = suspend T.(env: IKonveyorEnvironment) -> Sequence<S>
typealias SubKonveyorJoinerShortType<T, S> = suspend T.(joining: S) -> Unit
typealias SubKonveyorSplitterShortType<T, S> = suspend T.() -> Sequence<S>
