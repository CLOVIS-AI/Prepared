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

import opensavvy.prepared.suite.TestDsl

/**
 * Logs a value in the test output.
 *
 * ### Example
 *
 * ```kotlin
 * test("My test") {
 *     val testUser = log(createTestUser())
 *     // …
 * }
 * ```
 *
 * ```text
 * » Logged ‘User(username=Bob, …)’
 * ```
 *
 * This function is especially useful if you use [Power Assert](https://kotlinlang.org/docs/power-assert.html),
 * as it allows displaying intermediary values of a complex expression.
 *
 * To enable Power Assert, follow [these steps](https://kotlinlang.org/docs/power-assert.html#apply-the-plugin)
 * then add:
 *
 * ```kotlin
 * powerAssert {
 *     functions = listOf(
 *         // …Add any other function you want to instrument…
 *         "opensavvy.prepared.suite.assertions.log",
 *     )
 * }
 * ```
 *
 * Then, in your tests:
 * ```kotlin
 * test("My test") {
 *     log(12 + 5)
 * }
 * ```
 *
 * ```text
 * » Logged ‘17’
 *   log(12 + 5)
 *          |
 *          17
 * ```
 *
 * You can use this function to get more context about complex expressions when a test fails.
 */
suspend fun <T> TestDsl.log(value: T): T {
	return log(value) { "" }
}

/**
 * Logs a value in the test output, with some [additionalInfo].
 *
 * ### Example
 *
 * ```kotlin
 * test("My test") {
 *     val testUser = log(createTestUser()) { "Created test user" }
 *     // …
 * }
 * ```
 *
 * ```text
 * » Logged ‘User(username=Bob, …)’
 *   Created test user
 * ```
 *
 * This function is especially useful if you use [Power Assert](https://kotlinlang.org/docs/power-assert.html),
 * as it allows displaying intermediary values of a complex expression.
 *
 * To enable Power Assert, follow [these steps](https://kotlinlang.org/docs/power-assert.html#apply-the-plugin)
 * then add:
 *
 * ```kotlin
 * powerAssert {
 *     functions = listOf(
 *         // …Add any other function you want to instrument…
 *         "opensavvy.prepared.suite.assertions.log",
 *     )
 * }
 * ```
 *
 * Then, in your tests:
 * ```kotlin
 * test("My test") {
 *     log(12 + 5) { "Important intermediary result" }
 * }
 * ```
 *
 * ```text
 * » Logged ‘17’
 *   Important intermediary result
 *   log(12 + 5)
 *          |
 *          17
 * ```
 *
 * You can use this function to get more context about complex expressions when a test fails.
 *
 * @param additionalInfo A lambda that generates a [String] that will be printed alongside the message.
 */
suspend fun <T> TestDsl.log(value: T, additionalInfo: () -> String): T {
	val message = additionalInfo()

	println(
		buildString {
			append("» Logged ‘$value’")

			if (message.isNotBlank()) {
				appendLine()
				append("  ")
				append(message.replace("\n\n", "\n").replace("\n", "\n  "))
			}
		}
	)

	return value
}
