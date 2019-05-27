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

@KonveyorTagMarker
open class KonveyorBuilder<T: Any>: HandlerBuilder<T>() {

    private val handlers: MutableList<IKonveyorHandler<T>> = mutableListOf()

    override fun build(): Konveyor<T> = Konveyor(matcher = matcher, handlers = handlers, timeout = timeout)

    override fun exec(block: KonveyorExecutorShortType<T>) {
        execEnv {
            block()
        }
    }

    override fun execEnv(block: KonveyorExecutorType<T>) {
        handlers.add(KonveyorHandlerWrapper(executor = block))
    }

    fun handler(block: HandlerBuilder<T>.() -> Unit) {
        val builder = HandlerBuilder<T>()
        builder.block()
        val handler = builder.build()
        handlers.add(handler)
    }

    fun add(handler: IKonveyorHandler<T>) {
        handlers.add(handler)
    }

    operator fun IKonveyorHandler<T>.unaryPlus() = this@KonveyorBuilder.add(this)

    fun konveyor(block: KonveyorBuilder<T>.() -> Unit) {
        val builder = KonveyorBuilder<T>()
        builder.block()
        val handler = builder.build()
        handlers.add(handler)
    }

    fun <S: Any> subKonveyor(block: SubKonveyorBuilder<T, S>.() -> Unit) {
        val builder = SubKonveyorBuilder<T, S>()
        builder.block()
        val handler = builder.buildNew()
        handlers.add(handler)
    }

}
