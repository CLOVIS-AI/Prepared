package opensavvy.prepared.compat.kotlinx.datetime

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import opensavvy.prepared.suite.Time
import opensavvy.prepared.suite.advanceByMillis
import opensavvy.prepared.suite.nowMillis

@ExperimentalCoroutinesApi
private class KotlinClock(private val scheduler: TestCoroutineScheduler) : Clock {
	override fun now(): Instant =
		Instant.fromEpochMilliseconds(scheduler.currentTime)
}

/**
 * Creates a [Clock] that follows the virtual time in this test.
 */
@ExperimentalCoroutinesApi
val Time.clock: Clock
	get() = KotlinClock(scheduler)

/**
 * Accesses the current virtual time within this test, as an [Instant].
 */
@ExperimentalCoroutinesApi
val Time.now: Instant
	get() = clock.now()

/**
 * Advances the virtual time until it reaches [instant].
 *
 * It is not possible to set the time to a date in the past.
 */
@ExperimentalCoroutinesApi
fun Time.set(instant: Instant) {
	val diff = instant.toEpochMilliseconds() - nowMillis
	require(diff >= 0) { "Cannot advance to $instant, which is in the past of the current virtual time, $now" }
	advanceByMillis(diff)
}

/**
 * Advances the virtual time until it reaches [isoString], formatted as an ISO 8601 timestamp.
 *
 * It is not possible to set the time to a date in the past.
 */
@ExperimentalCoroutinesApi
fun Time.set(isoString: String) {
	set(Instant.parse(isoString))
}

/**
 * Delays until the virtual time reaches [instant], executing all enqueued tasks in order.
 */
@ExperimentalCoroutinesApi
suspend fun Time.delayUntil(instant: Instant) {
	val diff = instant.toEpochMilliseconds() - nowMillis
	require(diff >= 0) { "Cannot delay until $instant, which is in the past of the current virtual time, $now" }
	delay(diff)
}
