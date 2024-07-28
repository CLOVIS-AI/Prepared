package opensavvy.prepared.compat.kotlinx.datetime

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import opensavvy.prepared.suite.Time
import opensavvy.prepared.suite.nowMillis

@ExperimentalCoroutinesApi
private class KotlinClock(private val scheduler: TestCoroutineScheduler) : Clock {
	override fun now(): Instant =
		Instant.fromEpochMilliseconds(scheduler.currentTime)
}

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
 * @see now Access the current time
 * @see set Set the current time
 */
@ExperimentalCoroutinesApi
val Time.clock: Clock
	get() = KotlinClock(scheduler)

/**
 * Accesses the current virtual time within this test, as an [Instant].
 *
 * ### Example
 *
 * ```kotlin
 * test("Access the current time") {
 *     println(time.now)
 * }
 * ```
 *
 * @see clock Pass a way to access the time to another system
 */
@ExperimentalCoroutinesApi
val Time.now: Instant
	get() = clock.now()

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
@ExperimentalCoroutinesApi
suspend fun Time.delayUntil(instant: Instant) {
	val diff = instant.toEpochMilliseconds() - nowMillis
	require(diff >= 0) { "Cannot delay until $instant, which is in the past of the current virtual time, $now" }
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
@ExperimentalCoroutinesApi
suspend fun Time.delayUntil(isoString: String) {
	delayUntil(Instant.parse(isoString))
}
