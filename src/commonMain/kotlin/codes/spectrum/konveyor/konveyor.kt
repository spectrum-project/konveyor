package codes.spectrum.konveyor

/**
 * Global method for creation of a conveyor
 */
fun <T> konveyor(body: KonveyorBuilder<T>.() -> Unit ): Konveyor<T> {
    val builder = KonveyorBuilder<T>()
    builder.body()
    return builder.build()
}
