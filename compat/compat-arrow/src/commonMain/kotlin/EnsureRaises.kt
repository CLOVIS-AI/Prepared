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
inline fun <Failure> checkRaises(expected: Failure, block: Raise<Any?>.() -> Any?) {
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
inline fun <reified Failure> checkRaises(block: Raise<Any?>.() -> Any?) {
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
@Deprecated("This function has been renamed checkThrows.", ReplaceWith("opensavvy.prepared.compat.arrow.core.checkRaises(expected, block)"), DeprecationLevel.WARNING)
inline fun <Failure> assertRaises(expected: Failure, block: Raise<Failure>.() -> Any?) =
	checkRaises(expected, block)

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
@Deprecated("This function has been renamed checkThrows.", ReplaceWith("opensavvy.prepared.compat.arrow.core.checkRaises<Failure>(block)"), DeprecationLevel.WARNING)
inline fun <reified Failure> assertRaisesWith(block: Raise<Any?>.() -> Any?) =
	checkRaises<Failure>(block)
