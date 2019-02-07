package codes.spectrum.konveyor

import kotlin.test.Test
import kotlin.test.assertFails
import kotlin.test.expect

internal class DefaultKonveyorEnvironmentTest {

    @Test
    fun hasTest() {
        DefaultKonveyorEnvironment["some"] = 12
        expect(12) {
            DefaultKonveyorEnvironment["some"]
        }
        expect(13) {
            DefaultKonveyorEnvironment.get("some1", 13)
        }
        expect(null) {
            DefaultKonveyorEnvironment.getOrNull<Int>("some1")
        }
        expect(14) {
            DefaultKonveyorEnvironment.get("some1") {
                14
            }
        }
        assertFails {
            val x: Int = DefaultKonveyorEnvironment["some1"]
        }
        assertFails {
            val x: Long = DefaultKonveyorEnvironment["some"]
        }
    }

}