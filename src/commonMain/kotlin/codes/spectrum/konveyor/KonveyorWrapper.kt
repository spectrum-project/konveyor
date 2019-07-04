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
 * SubKonveyor class that includes all workflow of the konveyor
 */
class KonveyorWrapper<T : Any>(
    private val matcher: KonveyorMatcherType<T> = { true },
    private val executor: suspend KonveyorWrapper<T>.(T, IKonveyorEnvironment) -> Unit = { ctx: T, env: IKonveyorEnvironment ->
        defaultExecutor(ctx, env)
    },
    private val failer: suspend KonveyorWrapper<T>.(T, Throwable, IKonveyorEnvironment) -> Unit = { ctx: T, e: Throwable, env: IKonveyorEnvironment ->
        defaultFailer(ctx, e, env)
    }
) : IKonveyorHandler<T> {

    override fun match(context: T, env: IKonveyorEnvironment): Boolean = context.matcher(env)

    override suspend fun exec(context: T, env: IKonveyorEnvironment) = try {

    } catch (e: Throwable) {

    }

    suspend fun defaultExecutor(context: T, env: IKonveyorEnvironment) {

    }

    private fun defaultFailer(ctx: T, e: Throwable, env: IKonveyorEnvironment) {
        throw e
    }
}

