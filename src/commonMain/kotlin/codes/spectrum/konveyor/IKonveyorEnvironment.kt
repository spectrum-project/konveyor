package codes.spectrum.konveyor

/**
 * Interface describing environment for Konveyor. In the environment you can pass database connections,
 * environment variables and other entities which are not worth to be stored in context.
 */
interface IKonveyorEnvironment {

    /**
     * Checks if the element of environment with name `name` is stored
     */
    fun has(name:String):Boolean

    /**
     * Returns the value of the environment's element with name `name`
     */
    fun <T> get(name:String): T

//    fun has(serviceClass: Class<*>) : Boolean
//    fun <T:Any> get(serviceClass: Class<T>) : T
}
