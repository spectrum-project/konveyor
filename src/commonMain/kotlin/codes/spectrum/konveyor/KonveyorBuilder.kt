package codes.spectrum.konveyor

@KonveyorTagMarker
open class KonveyorBuilder<T> {

    val handlers: MutableList<IKonveyorHandler<T>> = mutableListOf()

    open fun build(): Konveyor<T> = Konveyor(handlers)

    fun exec(block: suspend T.() -> Unit) {
        handlers.add(KonveyorHandlerWrapper(executor = block))
    }

    fun handler(block: HandlerBuilder<T>.() -> Unit) {
        val builder = HandlerBuilder<T>()
        builder.block()
        val handler = builder.build()
        handlers.add(handler)
    }

    fun konveyor(block: KonveyorBuilder<T>.() -> Unit) {
        val builder = KonveyorBuilder<T>()
        builder.block()
        val handler = builder.build()
        handlers.add(handler)
    }

    fun <S> subKonveyor(block: SubKonveyorBuilder<T, S>.() -> Unit) {
        val builder = SubKonveyorBuilder<T, S>()
        builder.block()
        val handler = builder.buildNew()
        handlers.add(handler)
    }
}