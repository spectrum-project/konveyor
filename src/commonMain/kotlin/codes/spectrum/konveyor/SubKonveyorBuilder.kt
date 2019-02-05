package codes.spectrum.konveyor


@KonveyorTagMarker
class SubKonveyorBuilder<T,S>: KonveyorBuilder<S>() {

    private var splitter: suspend T.() -> Iterable<S> = { listOf<S>() }
    private var joiner: suspend T.(joining: S) -> Unit = { }

    fun buildNew(): SubKonveyorWrapper<T, S> = SubKonveyorWrapper(
        subKonveyor = build(),
        splitter = splitter,
        joiner = joiner
    )

    fun split(block: suspend T.() -> Iterable<S>) {
        splitter = block
    }

    fun join(block: suspend T.(joining: S) -> Unit) {
        joiner = block
    }

}
