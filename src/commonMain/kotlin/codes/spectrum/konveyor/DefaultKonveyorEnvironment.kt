package codes.spectrum.konveyor

import codes.spectrum.konveyor.exceptions.NotFoundEnvElementException
import kotlin.reflect.KClass

object DefaultKonveyorEnvironment : IKonveyorEnvironment {

    private val map = mutableMapOf<String, Pair<KClass<*>, Any>>()

    override fun has(name: String): Boolean = map.contains(name)

    override fun has(name: String, klazz: KClass<*>): Boolean = map[name]?.first == klazz ?: false

    override fun <T> get(name: String, klazz: KClass<*>): T = map[name]
        ?.let {
            if ( it.first == klazz) it.second as T else null
        } ?: throw NotFoundEnvElementException(name)

    fun setWithClass(name: String, klazz: KClass<*>, value: Any) {
        map[name] = klazz to value
    }

    inline operator fun <reified T: Any> get(name: String): T = (this as IKonveyorEnvironment).get(name)

    inline operator fun <reified T: Any> set(name: String, value: T) = setWithClass(name, T::class, value)
}
