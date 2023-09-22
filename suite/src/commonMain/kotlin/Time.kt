package opensavvy.prepared.suite

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.testTimeSource
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

/**
 * Time control helper. See [time].
 */
class Time private constructor(
	/**
	 * Access a time source which can be used to measure elapsed time, as controlled with [time].
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * test("Measure time") {
	 *     val start = time.source.markNow()
	 *     // …
	 *     val duration = start.elapsedNow()
	 * }
	 * ```
	 */
	val source: TimeSource.WithComparableMarks,

	/**
	 * Accessor for the underlying [TestCoroutineScheduler], which controls the current time.
	 */
	val scheduler: TestCoroutineScheduler,
) {

	@ExperimentalCoroutinesApi
	@ExperimentalTime
	constructor(environment: TestEnvironment) : this(environment.coroutineScope.testTimeSource, environment.coroutineScope.testScheduler)

}

/**
 * Time control center.
 *
 * ## Why?
 *
 * Often, we need to create algorithms that behave differently when executed at different times.
 * Using the system clock makes code much harder to test, or problems to reproduce.
 * Instead, it is recommended that algorithms take as input a "time generator", so fake implementations can be injected for testing.
 * - When we want to measure elapsed time, we should inject a [TimeSource] (see [Time.source]).
 * - When we want to access the current time, we should inject some kind of Clock (e.g. the one provided by `java.time`, or the one provided by `KotlinX.Datetime`, see the various extensions on [Time]).
 *
 * When executing the system under test, we need to provide such objects to the algorithm.
 * This attribute, [time], is the control center for generating such values and for controlling their outputs.
 *
 * ## Delay-skipping
 *
 * Inside tests, calls to [delay] are skipped in a way that keeps the order of events.
 * This makes tests much faster to execute without compromising on testing algorithms that need to wait for an event.
 *
 * This also applies to other time-related coroutine control functions, like [withTimeout].
 *
 * This allows to trivially implement algorithms which require skipping a large amount of time:
 * ```kotlin
 * test("Data is expired after 6 months") {
 *     val data = createSomeData()
 *     assertFalse(data.isExpired())
 *
 *     delay((6 * 30).days)
 *
 *     assertTrue(data.isExpired())
 * }
 * ```
 * Assuming all services use either the test clock or the test time source, the entire system will think 6 months have passed,
 * and all started tasks will have run the same number of times, and in the same order, as if 6 months had actually passed.
 *
 * To learn more about delay skipping, see the KotlinX.Coroutines' documentation: [runTest].
 *
 * > The delay-skipping behavior is controlled by [Time.scheduler].
 * > If you want to create your own coroutines, remember to add the scheduler to their [CoroutineContext], or they will delay for real.
 *
 * ## Time control
 *
 * Inside tests, a virtual time is available, that describes how much delay has been skipped.
 * A test always starts at the epoch.
 * [delay] allows us to move time forwards, executing all tasks as their execution date is reached.
 * We can also control the time directly.
 *
 * - [Time.nowMillis]: Access the current time in milliseconds (see the various `now*` accessors).
 * - [Time.advanceByMillis], [Time.advanceBy]: Advance the current time without executing the awaiting tasks.
 * - [Time.advanceUntilIdle]: Advance the current time, executing all tasks in order, until all tasks have been executed.
 * - [Time.runCurrent]: Run all tasks enqueued for the current time.
 *
 * ## Example
 *
 * This example checks that an event was recorded at the expected time:
 * ```kotlin
 * test("The event should be recorded at the current time") {
 *     val start = time.nowMillis
 *     val result = foo()
 *     val end = time.nowMillis
 *
 *     assert(result.timestamp > start)
 *     assert(result.timestamp < end)
 * }
 * ```
 *
 * @see Time.source Measure elapsed time
 * @see Time.nowMillis Current time, in milliseconds
 */
@ExperimentalCoroutinesApi
@ExperimentalTime
val TestDsl.time
	get() = Time(environment)

/**
 * Accesses the current time inside the test, in milliseconds.
 */
@ExperimentalCoroutinesApi
val Time.nowMillis: Long
	get() = scheduler.currentTime

/**
 * Advances the current time by [delay].
 */
@ExperimentalCoroutinesApi
fun Time.advanceByMillis(delay: Long) {
	scheduler.advanceTimeBy(delay)
}

/**
 * Advances the current time by [delay].
 *
 * ### Example
 *
 * ```kotlin
 * test("Hello world") {
 *     val start = time.source.markNow()
 *     time.advanceBy(2.minutes)
 *     val elapsed = start.elapsedNow()
 *
 *     assertEquals(2.minutes, elapsed)
 * }
 * ```
 */
@ExperimentalCoroutinesApi
fun Time.advanceBy(delay: Duration) {
	scheduler.advanceTimeBy(delay)
}

/**
 * Runs all enqueued tasks in the specified order, advancing the virtual time as needed until there are no more scheduled tasks.
 *
 * This is similar to `delay(Long.MAX_VALUE)`, except it leaves the virtual time on whenever the last task was scheduled.
 *
 * ### Example
 *
 * ```kotlin
 * test("Execute subtasks") {
 *     launch {
 *         delay(1000)
 *         println("A")
 *
 *         launch {
 *             delay(3000)
 *             println("B")
 *         }
 *     }
 *
 *     launch {
 *         delay(2000)
 *         println("C")
 *     }
 *
 *     time.advanceUntilIdle()
 *     assertEquals(4000, time.nowMillis)
 * }
 * ```
 */
@ExperimentalCoroutinesApi
fun Time.advanceUntilIdle() {
	scheduler.advanceUntilIdle()
}

/**
 * Runs all enqueued tasks at this moment in the virtual time.
 *
 * ### Example
 *
 * ```kotlin
 * test("Execute task") {
 *     launchInBackground {
 *         delay(1000)
 *         println("Hello world!")
 *     }
 *
 *     launchInBackground {
 *         delay(2000)
 *         println("Will never be printed")
 *     }
 *
 *     time.advanceByMillis(1000)
 *     time.runCurrent() // prints "Hello world!"
 * }
 * ```
 */
@ExperimentalCoroutinesApi
fun Time.runCurrent() {
	scheduler.runCurrent()
}
