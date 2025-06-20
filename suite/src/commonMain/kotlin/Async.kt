/*
 * Copyright (c) 2023-2025, OpenSavvy and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package opensavvy.prepared.suite

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * [CoroutineScope] for tasks started by this test.
 *
 * The test will only finish when all tasks started in this scope are finished.
 *
 * To start a single coroutine, see [launch].
 *
 * Tasks started in this scope respect the controlled [time].
 *
 * @see backgroundScope
 */
@PreparedDslMarker
val TestDsl.foregroundScope: CoroutineScope
	get() = environment.coroutineScope

/**
 * [CoroutineScope] for services started by this test.
 *
 * The test finishes when all tasks started in [foregroundScope] are finished.
 * If there are still tasks running in [backgroundScope], they are [cancelled][Job.cancel].
 *
 * This is useful to execute background services which are not part of the system-under-test, yet are expected to be running
 * by the system-under-test.
 *
 * To start a single coroutines, see [launchInBackground].
 *
 * Tasks started in this scope respect the controlled [time].
 *
 * @see foregroundScope
 */
@PreparedDslMarker
val TestDsl.backgroundScope: CoroutineScope
	get() = environment.coroutineScope.backgroundScope

/**
 * Starts a task in the [foregroundScope]. The test will wait for this task before finishing.
 *
 * By default, tasks started are run sequentially.
 * To execute tasks in parallel, explicitly use a [CoroutineDispatcher].
 *
 * The task will respect the controlled [time].
 *
 * @see launchInBackground
 */
@PreparedDslMarker
fun TestDsl.launch(
	context: CoroutineContext = EmptyCoroutineContext,
	start: CoroutineStart = CoroutineStart.DEFAULT,
	block: suspend CoroutineScope.() -> Unit,
) = foregroundScope.launch(CoroutineName("Test foreground task") + context, start, block)

/**
 * Starts a task in the [backgroundScope] scope. The test will **not** wait for this task before finishing.
 *
 * This is useful to start background services which are not part of the system-under-test, yet are expected to be running
 * by the system-under-test.
 *
 * By default, tasks started are run sequentially.
 * To execute tasks in parallel, explicitly use a [CoroutineDispatcher].
 *
 * The task will respect the controlled [time].
 *
 * @see launch
 */
@PreparedDslMarker
fun TestDsl.launchInBackground(
	context: CoroutineContext = EmptyCoroutineContext,
	start: CoroutineStart = CoroutineStart.DEFAULT,
	block: suspend CoroutineScope.() -> Unit,
) = backgroundScope.launch(CoroutineName("Test background task") + context, start, block)
