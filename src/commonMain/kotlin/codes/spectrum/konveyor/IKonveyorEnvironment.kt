package codes.spectrum.konveyor

import kotlin.reflect.KClass

/**
 * Interface describing environment for Konveyor. In the environment you can pass database connections,
 * environment variables and other entities which are not worth to be stored in context.
 */
interface IKonveyorEnvironment {

    /**
     * Checks if the element of environment with name `name` is stored
     */
    fun has(name:String): Boolean

    /**
     * Type safe check for the element of environment with name `name` is stored
     */
    fun has(name:String, klazz: KClass<*>): Boolean

    /**
     * Returns the value of the environment's element with name `name`
     */
    fun <T> get(name:String, klazz: KClass<*>): T

//    /**
//     * Returns either value of the environment's element with name `name` or `default` value
//     */
//    fun <T> get(name:String, default: T): T = if (has(name)) get(name) else default
//
//    /**
//     * Returns either value of the environment's element with name `name` or result of `default` lambda
//     */
//    fun <T> get(name:String, default: () -> T): T = if (has(name)) get(name) else default()
//
//    /**
//     * Returns either value of the environment's element with name `name` or `null`
//     */
//    fun <T> getOrNull(name:String): T? = if (has(name)) get(name) else null
//
//    /**
//     * Returns either value of the environment's element with name `name` or result of `default` lambda
//     */
//    fun getOrEmpty(name:String): String = get(name, "")

}

inline fun <reified T: Any> IKonveyorEnvironment.get(name: String): T = get(name, T::class)
inline fun <reified T: Any> IKonveyorEnvironment.get(name: String, default: T): T = try { get(name, T::class) } catch (e: Exception) { default }
inline fun <reified T: Any> IKonveyorEnvironment.get(name: String, default: () -> T): T = try { get(name, T::class) } catch (e: Exception) { default() }
inline fun <reified T: Any> IKonveyorEnvironment.getOrNull(name: String): T? = try { get(name, T::class) } catch (e: Exception) { null }

