package codes.spectrum.konveyor

import kotlin.reflect.KClass

object DefaultKonveyorEnvironment : IKonveyorEnvironment {

    override fun has(name: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun has(name: String, klazz: KClass<*>): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <T> get(name: String, klazz: KClass<*>): T {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
