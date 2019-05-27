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

import kotlinx.coroutines.withTimeout

class KonveyorHandlerWrapper<T>(
    private val matcher: KonveyorMatcherType<T> = { true },
    private val executor: KonveyorExecutorType<T>,
    private val timeout: Long = 0L
): IKonveyorHandler<T> {

    override fun match(context: T, env: IKonveyorEnvironment): Boolean = context.matcher(env)
    override suspend fun exec(context: T, env: IKonveyorEnvironment) {
        if (timeout > 0L) {
            withTimeout(timeout) {
                context.executor(env)
            }
        } else {
            context.executor(env)
        }
    }
}
