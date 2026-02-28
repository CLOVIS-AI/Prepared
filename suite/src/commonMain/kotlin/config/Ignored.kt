/*
 * Copyright (c) 2023-2026, OpenSavvy and contributors.
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

/**
 * Marks a test or an entire suite as disabled.
 *
 * ### Example
 *
 * Mark a suite as disabled:
 * ```kotlin
 * suite("Suite name", Ignored) {
 *     // …
 * }
 * ```
 *
 * Mark a test as disabled:
 * ```kotlin
 * test("Some kind of test", config = Ignored) {
 *     // …
 * }
 * ```
 *
 * @see Ignored.If Conditionally ignore a test or suite.
 */
object Ignored : TestConfig.Element, TestConfig.Key.Unique<Ignored> {
	override val key get() = this

	/**
	 * Marks this test or an entire suite as disabled if [predicate] returns `true`.
	 *
	 * If [predicate] returns `false`, does nothing.
	 *
	 * ### Example
	 *
	 * Mark a suite as disabled:
	 *
	 * ```kotlin
	 * suite("Suite name", Ignored.If { true }) {
	 *     // …
	 * }
	 * ```
	 *
	 * Mark a test as disabled:
	 *
	 * ```kotlin
	 * test("Some kind of test", Ignored.If { true }) {
	 *     // …
	 * }
	 * ```
	 *
	 * ### Use-case
	 *
	 * Conceptually, marking a test as ignored:
	 * ```kotlin
	 * test("Some kind of test", Ignored.If { someCondition }) {
	 *     // …
	 * }
	 * ```
	 * is similar to not declaring the test at all:
	 * ```kotlin
	 * if (!someCondition) {
	 *     test("Some kind of test") {
	 *         // …
	 *     }
	 * }
	 * ```
	 *
	 * However, some test runners (e.g., TestBalloon) refuse to execute suites that are declared without any tests.
	 * Declaring tests as conditionally ignored avoids this issue.
	 *
	 * @see Ignored Always ignore a test or suite.
	 * @see OnlyIf Opposite.
	 */
	@Suppress("FunctionName") // Looks like an 'object' on purpose: Ignored, Ignored.If {}
	inline fun If(predicate: () -> Boolean): TestConfig {
		return if (predicate()) Ignored else TestConfig.Empty
	}
}

/**
 * Executes this test or ensuite suite only if [predicate] returns `true`.
 *
 * Conceptually, this is the opposite of [Ignored.If].
 *
 * ### Example
 *
 * Only execute a suite if a JVM property is enabled:
 *
 * ```kotlin
 * suite("Suite name", OnlyIf { System.getProperty("foo") != null }) {
 *     // …
 * }
 * ```
 *
 * Only execute a test if a JVM property is enabled:
 *
 * ```kotlin
 * test("Some kind of test", OnlyIf { System.getProperty("foo") != null }) {
 *     // …
 * }
 * ```
 *
 * @see Ignored Ignore a test or suite.
 * @see Ignored.If Conditionally ignore a test or suite.
 */
@Suppress("FunctionName") // Similar to Ignored.If {}
inline fun OnlyIf(predicate: () -> Boolean): TestConfig =
	Ignored.If { !predicate() }
