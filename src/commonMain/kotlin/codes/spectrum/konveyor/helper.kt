package codes.spectrum.konveyor

/**
 * A multiplatform realization of a blocking call. It emulates behavior of `runBlocking` on other than JVM platforms
 */
expect fun <T> runMultiplatformBlocking(block: suspend () -> T): T
