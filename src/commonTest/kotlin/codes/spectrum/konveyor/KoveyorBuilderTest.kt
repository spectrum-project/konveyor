package codes.spectrum.konveyor

import kotlin.test.Test
import kotlin.test.assertEquals

class KoveyorBuilderTest {

    @Test
    fun testForBuilders() {
        val konvBuilder = konveyorBuilder<MyContext> {
            exec { x += 3 }
        }

        val executor = object: IKonveyorExecutor<MyContext> {
            override suspend fun exec(context: MyContext, env: IKonveyorEnvironment) {
                context.x += 12
            }

        }

        val konv = konveyor<MyContext> {
            exec { x += 7 }
            +executor
            +konveyorBuilder<MyContext> {
                exec { x += 113 }
            }
            +konvBuilder
            +konvBuilder {
                exec { x += 1021 }
            }
            handler { exec { x += 10117 } }
            konveyor { exec { x += 100321 } }
            subKonveyor<MySubContext> {
                split { sequenceOf(MySubContext(x=15)) }
                handler { exec { x += 17 } }
                exec { x += 172 }
                subKonveyor<MySubSubContext> {
                    on { false }
                }
                join { it -> x += it.x }
            }
            add { _: IKonveyorEnvironment ->
                x += 1000437
            }
            add { ->
                x += 1000385
            }
        }
        val context = MyContext(x=0)
        runMultiplatformBlocking { konv.exec(context) }

        assertEquals(7 +12 +113 +3 +3+1021 + 10117 + 100321 +15+17+172+1000437+1000385, context.x)
    }

    internal data class MyContext(var x: Int)
    internal data class MySubContext(var x: Int)
    internal data class MySubSubContext(var x: Int)
}