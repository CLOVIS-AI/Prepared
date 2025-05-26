package opensavvy.prepared.suite

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.testTimeSource
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.CoroutineContext
import kotlin.time.*
import kotlin.time.Duration.Companion.milliseconds

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
	 *
	 * Delay-skipping behavior is implemented by this dispatcher. Only `delay` calls within this dispatcher
	 * can be skipped and can advance the virtual time. That is, using code like `withContext(Dispatchers.IO) { … }`
	 * overrides the dispatcher and disables delay-skipping for the entire block.
	 *
	 * Sometimes, you may need to instantiate a service within your tests, that you want to internally use delay-skipping.
	 * In these situations, you may need to pass the scheduler:
	 *
	 * ```kotlin
	 * test("Test a cron service") {
	 *    val cron = CronService(coroutineContext = time.scheduler)
	 *    var witness = false
	 *
	 *    cron.runIn(2.minutes) { witness = true }
	 *
	 *    check(witness == false)
	 *    delay(2.minutes)
	 *    check(witness == true)
	 * }
	 * ```
	 *
	 * If the service expects a `CoroutineScope`, see [foregroundScope] and [backgroundScope] instead.
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
 *
 * ### Example
 *
 * ```kotlin
 * test("Using the virtual time") {
 *     val initial = time.nowMillis
 *
 *     // …do something…
 *     delay(5000)
 *
 *     check(time.nowMillis == initial + 5000)
 * }
 * ```
 *
 * For the specific use-case of measuring elapsed time, see [Time.source].
 *
 * @see [now] Access the current time as an [Instant].
 */
@ExperimentalCoroutinesApi
val Time.nowMillis: Long
	get() = scheduler.currentTime

/**
 * Access the current virtual time within this test, as an [Instant].
 *
 * ### Example
 *
 * ```kotlin
 * test("Using the virtual time") {
 *     val initial = time.now
 *
 *     // …do something…
 *     delay(5000)
 *
 *     check(time.now == initial + 5000)
 * }
 * ```
 *
 * For the specific use-case of measuring elapsed time, see [Time.source].
 *
 * @see clock To pass the time to another system.
 */
@ExperimentalCoroutinesApi
@ExperimentalTime
val Time.now: Instant
	get() = clock.now()

/**
 * Creates a [Clock] that follows the virtual time in this test.
 *
 * ### Example
 *
 * ```kotlin
 * test("Pass the time to an external service") {
 *     val service = SomeExternalService(time.clock)
 * }
 * ```
 *
 * @see Time.now The current time as an [Instant].
 * @see Time.source Measure elapsed time.
 */
@ExperimentalCoroutinesApi
@ExperimentalTime
val Time.clock: Clock
	get() = KotlinClock(scheduler)

@ExperimentalTime
@ExperimentalCoroutinesApi
private class KotlinClock(private val scheduler: TestCoroutineScheduler) : Clock {
	override fun now(): Instant =
		Instant.fromEpochMilliseconds(scheduler.currentTime)
}

/**
 * Advances the virtual time until it reaches [instant].
 *
 * ### Comparison with delayUntil
 *
 * This function is identical in behavior to [delayUntil].
 * It exists because tests often read better when using it to set the initial date:
 * ```kotlin
 * test("Some test") {
 *     // Given:
 *     time.set(Instant.parse("2024-02-13T21:32:41Z"))
 *
 *     // When:
 *     // …
 *     delayUntil(Instant.parse("2024-02-13T21:35:01Z"))
 *     // …
 *
 *     // Then:
 *     // …
 * }
 * ```
 *
 * We recommend using [set] to set the initial date at the very start of a test, and using [delayUntil] inside the test
 * logic.
 *
 * It is not possible to set the time to a date in the past.
 */
@ExperimentalTime
@ExperimentalCoroutinesApi
suspend fun Time.set(instant: Instant) {
	delayUntil(instant)
}

/**
 * Advances the virtual time until it reaches [isoString], formatted as an ISO 8601 timestamp.
 *
 * It is not possible to set the time to a date in the past.
 *
 * ### Example
 *
 * ```kotlin
 * test("Everything should behave the same on December 31st") {
 *     time.set("2022-12-31T23:37:00Z")
 *
 *     // …
 * }
 * ```
 *
 * ### Comparison with delayUntil
 *
 * This function is identical in behavior to [delayUntil].
 * It exists because tests often read better when using it to set the initial date:
 * ```kotlin
 * test("Some test") {
 *     // Given:
 *     time.set("2024-02-13T21:32:41Z")
 *
 *     // When:
 *     // …
 *     delayUntil("2024-02-13T21:35:01Z")
 *     // …
 *
 *     // Then:
 *     // …
 * }
 * ```
 *
 * We recommend using [set] to set the initial date at the very start of a test, and using [delayUntil] inside the test
 * logic.
 *
 * @see now Access the current time
 * @see delay Wait for some duration
 * @see delayUntil Wait for a specific time
 */
@ExperimentalTime
@ExperimentalCoroutinesApi
suspend fun Time.set(isoString: String) {
	set(Instant.parse(isoString))
}

/**
 * Delays until the virtual time reaches [instant], executing all enqueued tasks in order.
 *
 * `delayUntil` is useful to artificially trigger time-dependent algorithms.
 * To set the initial time at the start of the test, use [set].
 */
@ExperimentalTime
@ExperimentalCoroutinesApi
suspend fun Time.delayUntil(instant: Instant) {
	val diff = instant - now
	require(diff >= 0.milliseconds) { "Cannot delay until $instant, which is in the past of the current virtual time, $now" }
	delay(diff)
}

/**
 * Delays until the virtual time reaches [isoString], formatted as an ISO 8601 timestamp, executing all enqueued tasks in order.
 *
 * `delayUntil` is useful to artificially trigger time-dependent algorithms.
 * To set the initial time at the start of the test, use [set].
 *
 * @see set Set the current time
 * @see now Access the current time
 */
@ExperimentalTime
@ExperimentalCoroutinesApi
suspend fun Time.delayUntil(isoString: String) {
	delayUntil(Instant.parse(isoString))
}

/**
 * Advances the current time by [delay].
 *
 * ### Stability warning
 *
 * The KotlinX.Coroutines team is considering removing this functionality.
 * Learn more in [#3919](https://github.com/Kotlin/kotlinx.coroutines/issues/3919).
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
 *
 * ### Stability warning
 *
 * The KotlinX.Coroutines team is considering removing this functionality.
 * Learn more in [#3919](https://github.com/Kotlin/kotlinx.coroutines/issues/3919).
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
 *
 * ### Background tasks
 *
 * This method advances time until all the last [foreground tasks][TestDsl.launch]'s scheduled time.
 * There may be [background tasks][TestDsl.launchInBackground] that are scheduled for later.
 *
 * ### Stability warning
 *
 * The KotlinX.Coroutines team is considering removing this functionality.
 * Learn more in [#3919](https://github.com/Kotlin/kotlinx.coroutines/issues/3919).
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
 *
 * ### Stability warning
 *
 * The KotlinX.Coroutines team is considering removing this functionality.
 * Learn more in [#3919](https://github.com/Kotlin/kotlinx.coroutines/issues/3919).
 */
@ExperimentalCoroutinesApi
fun Time.runCurrent() {
	scheduler.runCurrent()
}
