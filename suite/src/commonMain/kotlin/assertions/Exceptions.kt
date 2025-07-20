/*
 * Copyright (c) 2025, OpenSavvy and contributors.
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

package opensavvy.prepared.suite.assertions

import opensavvy.prepared.suite.PreparedDslMarker

/**
 * Verifies that [block] throws an exception of type [T].
 *
 * If [block] indeed throws an exception of type [T], it is returned.
 * In all other cases, an [AssertionError] is thrown.
 *
 * ### Example
 *
 * ```kotlin
 * checkThrows<IllegalStateException> {
 *     yourService()
 * }
 * ```
 *
 * If the expected exception is thrown, it is returned by the function, so you can write further assertions.
 * For example, if you want an exception with a specific message, write:
 * ```kotlin
 * val e = checkThrows<IllegalStateException> {
 *     yourService()
 * }
 * check(e.message = "Cannot create a new user because another one already exists")
 * ```
 */
@PreparedDslMarker
inline fun <reified T : Throwable> checkThrows(block: () -> Any?): T {
	val result = try {
		block()
	} catch (e: Throwable) { // Simplifiable in Kotlin 2.2.20: https://youtrack.jetbrains.com/issue/KT-54363
		when (e) {
			is T -> return e
			else -> throw AssertionError("Expected to throw ${T::class}, but the operation threw the exception $e (see cause below for details)", e)
		}
	}
	throw AssertionError("Expected to throw ${T::class}, but the operation was successful and returned: $result")
}
