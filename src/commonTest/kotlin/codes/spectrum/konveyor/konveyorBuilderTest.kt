/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package codes.spectrum.konveyor

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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
                            println("gen: $it")
                            MySubContext(
                                subId = it.toString(),
                                subValue = it
                            )
                        }
                        .asSequence()
                }

                exec {
                    println("*2: $subValue")
                    subValue *= 2
                }

                join { joining: MySubContext ->
                    println("merge: ${joining.subValue}")
                    value += joining.subValue.toInt()
                }
            }
        }

        runMultiplatformBlocking { conveyor.exec(myContext) }

        assertEquals(79, myContext.value)

    }

    @Test
    @Ignore
    // Test is ignored since SubKonveyorBuilder::on is not working
    fun `subKonveyorTest use on`() {
        val myContext = MyContext(id = "1", value = 1, list = mutableListOf(12L, 13L, 14L))
        val conveyor = konveyor<MyContext> {
            subKonveyor<MySubContext> {
                on { false }
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

        assertEquals(1, myContext.value)

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

    @Test
    fun addHandlerTest() {
        val myContext = MyContext(id = "1", value = 1)
        val conveyor = konveyor<MyContext> {
            add(SomeHandler())
            +SomeHandler()
        }

        runMultiplatformBlocking { conveyor.exec(myContext) }

        assertEquals(2001, myContext.value)
    }

    @Test
    fun addKonveyorTest() {
        val myContext = MyContext(id = "1", value = 1)
        val addedKonveyor = konveyor<MyContext> {
            add(SomeHandler())
            +SomeHandler()
        }
        val conveyor = konveyor<MyContext> {
            add(addedKonveyor)
            +addedKonveyor
        }

        runMultiplatformBlocking { conveyor.exec(myContext) }

        assertEquals(4001, myContext.value)
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

    internal class SomeHandler(): IKonveyorHandler<MyContext> {
        override fun match(context: MyContext, env: IKonveyorEnvironment): Boolean = true

        override suspend fun exec(context: MyContext, env: IKonveyorEnvironment) {
            context.value += 1000
        }

    }

}
