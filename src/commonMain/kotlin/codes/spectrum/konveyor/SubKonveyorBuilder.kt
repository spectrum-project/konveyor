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

import kotlin.coroutines.EmptyCoroutineContext


@KonveyorTagMarker
class SubKonveyorBuilder<T: Any,S: Any>: BaseBuilder<T>(), IHandlerContainerBuilder<S> {
    private var splitter: SubKonveyorSplitterType<T, S> = { sequence {  } }
    private var joiner: SubKonveyorJoinerType<T, S> = { _: S, _: IKonveyorEnvironment -> }
    private var contexter: SubKonveyorCoroutineContextType<T> = { EmptyCoroutineContext }
    private var bufferSizer: SubKonveyorCoroutineBufferSize<T> = { 1 }
    private var consumer: SubKonveyorCoroutineConsumer<T> = { 1 }
    private var handlers: MutableList<IKonveyorHandler<S>> = mutableListOf()

    override fun build(): SubKonveyorWrapper<T, S> = SubKonveyorWrapper(
        matcher = matcher,
        handlers = handlers,
        splitter = splitter,
        joiner = joiner,
        bufferSizer = bufferSizer,
        contexter = contexter,
        consumer = consumer
    )

    fun bufferSize(block: SubKonveyorCoroutineBufferSize<T>) { bufferSizer = block}
    fun coroutineContext(block: SubKonveyorCoroutineContextType<T>) { contexter = block}
    fun joinersNumber(block: SubKonveyorCoroutineConsumer<T>) { consumer = block}

    fun split(block: SubKonveyorSplitterShortType<T, S>) {
        splitEnv { env ->
            block()
        }
    }

    fun splitEnv(block: SubKonveyorSplitterType<T, S>) {
        splitter = block
    }

    fun join(block: SubKonveyorJoinerShortType<T, S>) {
        joinEnv { joining, env ->
            block(joining)
        }
    }

    fun joinEnv(block: SubKonveyorJoinerType<T, S>) {
        joiner = block
    }

    override fun add(handler: IKonveyorHandler<S>) {
        handlers.add(handler)
    }

}
