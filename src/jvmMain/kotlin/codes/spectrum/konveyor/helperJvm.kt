package codes.spectrum.konveyor

import kotlinx.coroutines.runBlocking

actual fun <T> runMultiplatformBlocking(block: suspend () -> T): T = runBlocking { block() }
