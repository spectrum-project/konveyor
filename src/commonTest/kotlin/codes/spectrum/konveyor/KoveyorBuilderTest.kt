package codes.spectrum.konveyor

import kotlin.test.Test

class KoveyorBuilderTest {

    @Test
    fun testForBuilders() {
        val konvBuilder: IKonveyorBuilder<MyContext> = konveyorBuilder<MyContext> {
            exec {  }
        }

        val konv = konveyor<MyContext> {
            exec {  }
            +konvBuilder
            +konvBuilder {

            }
            handler {  }
            konveyor {  }
            subKonveyor<MySubContext> {
                handler {  }
                exec {  }
                subKonveyor<MySubSubContext> {  }
            }
            add { _: IKonveyorEnvironment ->
                println()
            }
            add { ->
                println()
            }
        }
    }

    internal data class MyContext(var x: Int)
    internal data class MySubContext(var x: Int)
    internal data class MySubSubContext(var x: Int)
}