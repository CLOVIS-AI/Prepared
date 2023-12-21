package opensavvy.prepared.compat.java.time

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestCoroutineScheduler
import opensavvy.prepared.suite.Time
import opensavvy.prepared.suite.advanceByMillis
import opensavvy.prepared.suite.nowMillis
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

@ExperimentalCoroutinesApi
private class JavaClock(private val scheduler: TestCoroutineScheduler, private val timeZone: ZoneId) : Clock() {
	override fun instant(): Instant =
		Instant.ofEpochMilli(scheduler.currentTime)

	override fun withZone(zone: ZoneId): Clock =
		JavaClock(scheduler, zone)

	override fun getZone(): ZoneId =
		timeZone
}

/**
 * Creates a [Clock] that follows the virtual time in this test.
 */
@ExperimentalCoroutinesApi
val Time.clockJava: Clock
	get() = JavaClock(scheduler, ZoneOffset.UTC)

/**
 * Accesses the current virtual time within this test, as an [Instant].
 */
@ExperimentalCoroutinesApi
val Time.nowJava: Instant
	get() = clockJava.instant()

/**
 * Advances the virtual time until it reaches [instant].
 *
 * It is not possible to set the time to a date in the past.
 */
@ExperimentalCoroutinesApi
fun Time.set(instant: Instant) {
	val diff = instant.toEpochMilli() - nowMillis
	require(diff >= 0) { "Cannot advance to $instant, which is in the past of the current virtual time, $nowJava" }
	advanceByMillis(diff)
}

/**
 * Delays until the virtual time reaches [instant], executing all enqueued tasks in order.
 */
@ExperimentalCoroutinesApi
suspend fun Time.delayUntil(instant: Instant) {
	val diff = instant.toEpochMilli() - nowMillis
	require(diff >= 0) { "Cannot delay until $instant, which is in the past of the current virtual time, $nowJava" }
	delay(diff)
}
