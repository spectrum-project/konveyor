package codes.spectrum.konveyor

import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.expect

class EnvKonveyorTest {

    @Test
    fun koveyorWithEnv() {
        val konveyor = konveyor<MyContext> {
            execEnv { env ->
                y += env.get<Int>("increment")
            }
        }

        val context = MyContext()

        expect(MyContext(0, 8)) {
            runMultiplatformBlocking { konveyor.exec(context, env = MyEnv) }
            context
        }
    }

    @Test
    fun subKoveyorWithEnv() {
        val konveyor = konveyor<MyContext> {
            subKonveyor<MySubContext> {
                splitEnv { env ->
                    (1..25)
                        .map {
                            MySubContext()
                        }
                        .asSequence()
                }
                execEnv { env ->
                    z += env.get<Int>("increment")
                }
                handler {
                    onEnv { env ->
                        z == env.get<Int>("increment")
                    }
                    execEnv { env ->
                        k += env.get<Int>("increment")
                    }
                }
                joinEnv { joining, env ->
                    x += joining.z + env.get<Int>("increment")
                    y += joining.k + 2*env.get<Int>("increment")
                }
            }
        }

        val context = MyContext()

        expect(MyContext(400, 600)) {
            runMultiplatformBlocking { konveyor.exec(context, env = MyEnv) }
            context
        }
    }

    internal object MyEnv : IKonveyorEnvironment {
        val increment = 8

        override fun has(name: String): Boolean = name == "increment"

        override fun has(name: String, klazz: KClass<*>): Boolean = name == "increment" && klazz == Int::class

        override fun <T> get(name: String, klazz: KClass<*>): T = when {
            klazz == Int::class -> increment as T
            else -> throw RuntimeException("Wrong class for $name")
        }
    }

    internal data class MyContext(
        var x: Int = 0,
        var y: Int = 0
    )

    internal data class MySubContext(
        var z: Int = 0,
        var k: Int = 0
    )

}