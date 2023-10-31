package opensavvy.prepared.compat.arrow.core

import arrow.core.getOrElse
import arrow.core.raise.ExperimentalTraceApi
import arrow.core.raise.Raise
import arrow.core.raise.either
import opensavvy.prepared.suite.PreparedDslMarker
import opensavvy.prepared.suite.TestDsl

/**
 * Fails the test if [block] raises.
 */
@Suppress("UnusedReceiverParameter") // we're keeping the receiver for scoping
@ExperimentalTraceApi
@PreparedDslMarker
inline fun <Failure, Success> TestDsl.failOnRaise(block: Raise<Failure>.() -> Success): Success {
	return either(block)
		.getOrElse { throw AssertionError("Expected the block to execute successfully, but a value was raised: $it") }
}
