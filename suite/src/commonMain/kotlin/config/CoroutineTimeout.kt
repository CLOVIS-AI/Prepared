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

package opensavvy.prepared.suite.config

import opensavvy.prepared.suite.TestDsl
import opensavvy.prepared.suite.launch
import opensavvy.prepared.suite.launchInBackground
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Defines for how long nothing is allowed to happen before the test is automatically stopped.
 *
 * Note that this only applies to nothing happening in the coroutine dispatcher managed by the Coroutines
 * system: [launch][TestDsl.launch], [launchInBackground][TestDsl.launchInBackground], etc.
 *
 * For example, this test never finishes, but will not be stopped by this timeout, because coroutines are still being
 * executed:
 * ```kotlin
 * while(true) {
 *     delay(1000)
 * }
 * ```
 */
data class CoroutineTimeout(
	val duration: Duration,
) : TestConfig.Element {

	override val key get() = Companion

	companion object : TestConfig.Key.Unique<CoroutineTimeout> {
		val Default = 10.seconds
	}
}

internal fun TestConfig.effectiveTimeout() = get(CoroutineTimeout)?.duration ?: CoroutineTimeout.Default
