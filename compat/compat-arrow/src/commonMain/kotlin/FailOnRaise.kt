package opensavvy.prepared.compat.arrow.core

import arrow.core.Either
import arrow.core.raise.ExperimentalTraceApi
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.traced
import opensavvy.prepared.suite.PreparedDslMarker

/**
 * Fails the test if [block] raises.
 *
 * ### Example
 *
 * ```kotlin
 * test("√4 does not raise") {
 *    failOnRaise {
 *        sqrt(4.0)
 *    } shouldBe 2.0
 * }
 * ```
 */
@ExperimentalTraceApi
@PreparedDslMarker
inline fun <Failure, Success> failOnRaise(block: Raise<Failure>.() -> Success): Success {
	val result = either {
		traced(block) { trace, failure ->
			throw AssertionError("An operation raised $failure\n${trace.stackTraceToString()}")
				.apply {
					for (suppressed in trace.suppressedExceptions())
						addSuppressed(suppressed)
				}
		}
	}

	check(result is Either.Right) { "Impossible situation: we throw an error when the passed block raises, but it still gaves us a failed either: $result" }
	return result.value
}
