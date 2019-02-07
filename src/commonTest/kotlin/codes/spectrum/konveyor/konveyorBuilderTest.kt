package codes.spectrum.konveyor

import kotlin.test.Test
import kotlin.test.assertEquals

class konveyorBuilderTest {

    @Test
    fun execTest() {
        val myContext = MyContext(id = "1", value = 1)
        val conveyor = konveyor<MyContext> {
            exec {
                value++
            }
        }

        runMultiplatformBlocking { conveyor.exec(myContext) }

        assertEquals(2, myContext.value)

    }

    @Test
    fun handlerTest() {
        val myContext1 = MyContext(id = "1", value = 1)
        val myContext2 = MyContext(id = "2", value = 1)
        val conveyor = konveyor<MyContext> {
            handler {
                on {
                    id == "1"
                }
                exec {
                    value++
                }
            }
        }

        runMultiplatformBlocking { conveyor.exec(myContext1) }
        runMultiplatformBlocking { conveyor.exec(myContext2) }

        assertEquals(2, myContext1.value)
        assertEquals(1, myContext2.value)

    }

    @Test
    fun subKonveyorTest() {
        val myContext = MyContext(id = "1", value = 1, list = mutableListOf(12L, 13L, 14L))
        val conveyor = konveyor<MyContext> {
            subKonveyor<MySubContext> {

                split {
                    list
                        .map {
                            MySubContext(
                                subId = it.toString(),
                                subValue = it
                            )
                        }
                        .asSequence()
                }

                exec {
                    subValue *= 2
                }

                join { joining: MySubContext ->
                    value += joining.subValue.toInt()
                }
            }
        }

        runMultiplatformBlocking { conveyor.exec(myContext) }

        assertEquals(79, myContext.value)

    }

    @Test
    fun emptySubKonveyorTest() {
        val myContext = MyContext(id = "1", value = 1, list = mutableListOf(12L, 13L, 14L))
        val conveyor = konveyor<MyContext> {
            subKonveyor<MySubContext> {
            }
        }

        runMultiplatformBlocking { conveyor.exec(myContext) }

        assertEquals(1, myContext.value)

    }

    @Test
    fun konveyorTest() {
        val myContext = MyContext(id = "1", value = 1)
        val conveyor = konveyor<MyContext> {
            exec { value = 12 }
            konveyor {
                exec {
                    value *= 2
                }
                handler {
                    on { value <= 10 }
                    exec { value *= 4 }
                }
                handler {
                    on { value > 10 }
                    exec { value /= 4 }
                }
            }
        }

        runMultiplatformBlocking { conveyor.exec(myContext) }

        assertEquals(6, myContext.value)

    }

    internal data class MyContext(
        var id: String = "",
        var value: Int = 0,
        var list: MutableList<Long> = mutableListOf()
    )

    internal data class MySubContext(
        var subId: String = "",
        var subValue: Long = 0
    )

}