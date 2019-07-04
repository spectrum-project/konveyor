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

/**
 * The builder which builds Handler object
 *
 */
@KonveyorTagMarker
open class HandlerBuilder<T: Any>: BaseBuilder<T>(), IHandlerBuilder<T> {

    private var executor: KonveyorExecutorType<T> = { }

    /**
     * With this methos one can set the lambda for matcher [[IKonveyorHandler.match]] having access to
     * [[IKonveyorEnvironment]] through lambda parameter to the handler
     */
    override fun onEnv(block: KonveyorMatcherType<T>) {
        matcher = block
    }

    /**
     * With this methods one can set the lambda for executor [[IKonveyorHandler.exec]] having access to
     * [[IKonveyorEnvironment]] through lambda parameter to the handler
     */
    override fun execEnv(block: KonveyorExecutorType<T>) {
        executor = block
    }

    /**
     * Builds the [[IKonveyorHandler]] implementation
     */
    override fun build(): IKonveyorHandler<T> = KonveyorHandlerWrapper<T>(
        matcher = matcher,
        executor = executor,
        timeout = timeout
    )
}
