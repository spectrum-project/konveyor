package codes.spectrum.konveyor

import org.junit.jupiter.api.Test

internal class HelperJvmKtTest {

    @Test
    fun runMultiplatformBlockingTest() {
        runMultiplatformBlocking { println("OK") }
    }
}