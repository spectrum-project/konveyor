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

import kotlin.coroutines.CoroutineContext

fun <T: Any> konveyorBuilder(body: KonveyorBuilder<T>.() -> Unit ): KonveyorBuilder<T> = KonveyorBuilder<T>().apply(body)

/**
 * Global method for creation of a conveyor
 */
fun <T: Any> konveyor(body: KonveyorBuilder<T>.() -> Unit ): Konveyor<T> = konveyorBuilder(body).build()

/**
 * Global method for creation of a handler instance
 */
fun <T: Any> handler(body: HandlerBuilder<T>.() -> Unit ): IKonveyorHandler<T> {
    val builder = HandlerBuilder<T>()
    builder.body()
    return builder.build()
}

typealias KonveyorExecutorType<T> = suspend T.(IKonveyorEnvironment) -> Unit
typealias KonveyorMatcherType<T> = T.(IKonveyorEnvironment) -> Boolean
typealias KonveyorExecutorShortType<T> = suspend T.() -> Unit
typealias KonveyorMatcherShortType<T> = T.() -> Boolean
typealias KonveyorTimeoutType = () -> Long

typealias SubKonveyorJoinerType<T, S> = suspend T.(joining: S, env: IKonveyorEnvironment) -> Unit
typealias SubKonveyorSplitterType<T, S> = suspend T.(env: IKonveyorEnvironment) -> Sequence<S>
typealias SubKonveyorJoinerShortType<T, S> = suspend T.(joining: S) -> Unit
typealias SubKonveyorSplitterShortType<T, S> = suspend T.() -> Sequence<S>
typealias SubKonveyorCoroutineContextType<T> = suspend T.(env: IKonveyorEnvironment) -> CoroutineContext
typealias SubKonveyorCoroutineBufferSize<T> = suspend T.(env: IKonveyorEnvironment) -> Int
typealias SubKonveyorCoroutineConsumer<T> = suspend T.(env: IKonveyorEnvironment) -> Int
