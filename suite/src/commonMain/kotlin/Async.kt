package opensavvy.prepared.suite

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * [CoroutineScope] for tasks started by this test.
 *
 * The test will only finish when all tasks started in this scope are finished.
 *
 * Tasks started in this scope respect the controlled [time].
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
 * Tasks started in this scope respect the controlled [time].
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
 */
@PreparedDslMarker
fun TestDsl.launch(
	context: CoroutineContext = EmptyCoroutineContext,
	start: CoroutineStart = CoroutineStart.DEFAULT,
	block: suspend CoroutineScope.() -> Unit,
) = foregroundScope.launch(context, start, block)

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
 */
@PreparedDslMarker
fun TestDsl.launchInBackground(
	context: CoroutineContext = EmptyCoroutineContext,
	start: CoroutineStart = CoroutineStart.DEFAULT,
	block: suspend CoroutineScope.() -> Unit,
) = backgroundScope.launch(context, start, block)
