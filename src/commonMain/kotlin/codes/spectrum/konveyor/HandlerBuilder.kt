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

/**
 * The builder which builds Handler object
 *
 */
@KonveyorTagMarker
open class HandlerBuilder<T> {

    protected var matcher: KonveyorMatcherType<T> = { true }
    private var executor: KonveyorExecutorType<T> = { }
    protected var timeout: Long = 0L

    /**
     * With this methos one can set the lambda for matcher [[IKonveyorHandler.match]] to the handler
     */
    fun on(block: KonveyorMatcherShortType<T>) {
        onEnv { block() }
    }

    /**
     * With this methos one can set the lambda for matcher [[IKonveyorHandler.match]] having access to
     * [[IKonveyorEnvironment]] through lambda parameter to the handler
     */
    fun onEnv(block: KonveyorMatcherType<T>) {
        matcher = block
    }

    /**
     * With this methods one can set the lambda for executor [[IKonveyorHandler.exec]] to the handler
     */
    open fun exec(block: KonveyorExecutorShortType<T>) {
        execEnv {
            block()
        }
    }

    /**
     * With this methods one can set the lambda for executor [[IKonveyorHandler.exec]] having access to
     * [[IKonveyorEnvironment]] through lambda parameter to the handler
     */
    open fun execEnv(block: KonveyorExecutorType<T>) {
        executor = block
    }

    /**
     * With this methods one can set timeout in millisecond([[Long]]) for handler and throw [[TimeoutCancellationException]] if time out
     */
    fun timeout(block: KonveyorTimeoutType) {
        timeout = block()
    }

    /**
     * Builds the [[IKonveyorHandler]] implementation
     */
    open fun build(): IKonveyorHandler<T> = KonveyorHandlerWrapper<T>(
        matcher = matcher,
        executor = executor,
        timeout = timeout
    )
}
