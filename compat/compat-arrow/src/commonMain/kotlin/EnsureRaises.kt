package opensavvy.prepared.compat.arrow.core

import arrow.core.raise.Raise
import arrow.core.raise.either
import opensavvy.prepared.suite.PreparedDslMarker

/**
 * Fails the test if [block] doesn't raise with [expected].
 *
 * ### Example
 *
 * ```kotlin
 * test("√-1 raises") {
 *     assertRaises(NegativeSquareRoot) {
 *         sqrt(-1.0)
 *     }
 * }
 * ```
 */
@PreparedDslMarker
inline fun <Failure> assertRaises(expected: Failure, block: Raise<Failure>.() -> Any?) {
	either(block).fold(
		ifLeft = {
			if (it != expected)
				throw AssertionError("Expected to fail with $expected, but failed with $it")
			// else: successful case
		},
		ifRight = {
			throw AssertionError("Expected to fail with $expected, but the operation was successful and returned $it")
		},
	)
}

/**
 * Fails the test if [block] doesn't raise with a value of type [Failure].
 *
 * ### Example
 *
 * ```kotlin
 * test("√-1 raises") {
 *     assertRaisesWith<NegativeSquareRoot> {
 *         sqrt(-1.0)
 *     }
 * }
 * ```
 */
@PreparedDslMarker
inline fun <reified Failure> assertRaisesWith(block: Raise<Any?>.() -> Any?) {
	either(block).fold(
		ifLeft = {
			if (it !is Failure)
				throw AssertionError("Expected to fail with ${Failure::class}, but failed with $it")
			// else: successful case
		},
		ifRight = {
			throw AssertionError("Expected to fail with ${Failure::class}, but the operation was successful and returned $it")
		},
	)
}
