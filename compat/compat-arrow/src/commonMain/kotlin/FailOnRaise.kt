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
			var stackTrace = trace.stackTraceToString()

			// The trace starts with a big warning from Arrow about catching its internal exception.
			// This is actually normal behavior of the Arrow library: https://github.com/arrow-kt/arrow/issues/3388
			// Since there is nothing to worry about for the user, we just hide it.
			if (stackTrace.startsWith("arrow.core.raise.Traced: kotlin.coroutines.cancellation.CancellationException should never get swallowed.") || stackTrace.startsWith("Traced: kotlin.coroutines.cancellation.CancellationException should never get swallowed."))
				stackTrace = stackTrace.replaceBefore("\n", "").replaceFirst("\n", "")

			throw AssertionError("An operation raised $failure.\n$stackTrace")
				.apply {
					for (suppressed in trace.suppressedExceptions())
						addSuppressed(suppressed)
				}
		}
	}

	check(result is Either.Right) { "Impossible situation: we throw an error when the passed block raises, but it still gaves us a failed either: $result" }
	return result.value
}
