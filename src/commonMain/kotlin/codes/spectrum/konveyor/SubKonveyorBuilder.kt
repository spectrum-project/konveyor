package codes.spectrum.konveyor


@KonveyorTagMarker
class SubKonveyorBuilder<T,S>: KonveyorBuilder<S>() {

    private var splitter: SubKonveyorSplitterType<T, S> = { sequence {  } }
    private var joiner: SubKonveyorJoinerType<T, S> = { _: S, _: IKonveyorEnvironment -> }

    fun buildNew(): SubKonveyorWrapper<T, S> = SubKonveyorWrapper(
        subKonveyor = build(),
        splitter = splitter,
        joiner = joiner
    )

    fun split(block: SubKonveyorSplitterShortType<T, S>) {
        splitEnv { env ->
            block()
        }
    }

    fun splitEnv(block: SubKonveyorSplitterType<T, S>) {
        splitter = block
    }

    fun join(block: T.(S) -> Unit) {
        joinEnv { joining, env ->
            block(joining)
        }
    }

    fun joinEnv(block: SubKonveyorJoinerType<T, S>) {
        joiner = block
    }

}
