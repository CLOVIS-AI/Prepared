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

package opensavvy.prepared.compat.java.time

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestCoroutineScheduler
import opensavvy.prepared.suite.Time
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
 * Delays until the virtual time reaches [instant], executing all enqueued tasks in order.
 *
 * `delayUntil` is useful to artificially trigger time-dependent algorithms.
 * To set the initial time at the start of the test, use [set].
 *
 * It is not possible to set the time to a date in the past.
 */
@ExperimentalCoroutinesApi
suspend fun Time.delayUntil(instant: Instant) {
	val diff = instant.toEpochMilli() - nowMillis
	require(diff >= 0) { "Cannot delay until $instant, which is in the past of the current virtual time, $nowJava" }
	delay(diff)
}
