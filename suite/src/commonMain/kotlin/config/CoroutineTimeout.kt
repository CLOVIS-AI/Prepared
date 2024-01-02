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
