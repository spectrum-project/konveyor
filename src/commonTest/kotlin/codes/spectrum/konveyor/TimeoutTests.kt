package codes.spectrum.konveyor

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlin.test.Test
import kotlin.test.assertFailsWith

class TimeoutTests {
    @Test
    fun timeoutOkTest() {
        val myContext = konveyorBuilderTest.MyContext()
        val timeoutKonveyor = konveyor<konveyorBuilderTest.MyContext> {
            timeout { 1000 }
            exec { delay(100) }
        }

        runMultiplatformBlocking { timeoutKonveyor.exec(myContext) }
    }

    @Test
    fun timeoutCancelTest() {
        val myContext = konveyorBuilderTest.MyContext()
        val timeoutKonveyor = konveyor<konveyorBuilderTest.MyContext> {
            timeout { 100 }
            exec { delay(1000) }
        }
        assertFailsWith<TimeoutCancellationException> {
            runMultiplatformBlocking { timeoutKonveyor.exec(myContext) }
        }
    }

}