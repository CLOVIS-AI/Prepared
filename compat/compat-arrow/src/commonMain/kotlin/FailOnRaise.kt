package opensavvy.prepared.compat.arrow.core

import arrow.core.getOrElse
import arrow.core.raise.ExperimentalTraceApi
import arrow.core.raise.Raise
import arrow.core.raise.either
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
	return either(block)
		.getOrElse { throw AssertionError("Expected the block to execute successfully, but a value was raised: $it") }
}
