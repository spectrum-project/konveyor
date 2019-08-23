package codes.spectrum.konveyor

import kotlin.test.Test
import kotlin.test.assertEquals

class HandlerTest {

    @Test
    fun createTest() {
        val h1 = handler<MyContext> {
            on { true }
            exec { value += 100 }
        }
        val h2 = handler<MyContext> {
            on { false }
            exec { value += 120 }
        }

        val konv = konveyor<MyContext> {
            +h1
            +h2
        }
        val context = MyContext()
        runMultiplatformBlocking { konv.exec(context) }
        assertEquals(100, context.value)
    }

    internal data class MyContext(
        var id: String = "",
        var value: Int = 0
    )

    @Test
    fun handlerMatchTest() {
        val konveyor = konveyor<MyContext> {
            exec {
                value = 41
            }

            +MyHandler
            exec { assertEquals(-1, value) }

            +MyHandler
            exec { assertEquals(-1, value) }
        }

        runMultiplatformBlocking { konveyor.exec(MyContext()) }
    }

    @Test
    fun subKonveyorHandlerMatchTest() {
        val konveyor = konveyor<MyContext> {
            exec {
                value = 41
            }

            subKonveyor<MyContext> {
                split {
                    sequenceOf(this)
                }

                +MyHandler
                exec { assertEquals(-1, value) }

                +MyHandler
                exec { assertEquals(-1, value) }
            }
        }

        runMultiplatformBlocking { konveyor.exec(MyContext()) }
    }

    internal object MyHandler : IKonveyorHandler<MyContext> {
        override fun match(context: MyContext, env: IKonveyorEnvironment): Boolean = context.value >= 0

        override suspend fun exec(context: MyContext, env: IKonveyorEnvironment) {
            context.value -= 42
        }
    }
}
