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

package opensavvy.prepared.runner.kotlin

import opensavvy.prepared.suite.SuiteDsl
import opensavvy.prepared.suite.config.TestConfig

/**
 * Entrypoint to declare a [SuiteDsl] executed with [kotlin-test](https://kotlinlang.org/api/latest/kotlin.test/).
 *
 * Because `kotlin-test` doesn't provide a way to dynamically instantiate tests, we have to cheat.
 * This class abstracts away our hacks to make it work.
 * Please vote on [KT-46899](https://youtrack.jetbrains.com/issue/KT-46899).
 *
 * Because of these hacks, implementing this class has strange requirements:
 * - an implementation class's name must contain `Test`
 *
 * ### Example
 *
 * ```kotlin
 * class ExecuteTest : TestExecutor() {
 *     override fun Suite.register() {
 *         test("This is a test") {
 *             println("Hello world!")
 *         }
 *     }
 * }
 * ```
 */
expect abstract class TestExecutor() {

	/**
	 * Default configuration for all tests [registered][register] in this class.
	 */
	open val config: TestConfig

	/**
	 * Declares a [SuiteDsl] which will be run with `kotlin-test`.
	 *
	 * For more information, see the [class-level documentation][TestExecutor].
	 */
	abstract fun SuiteDsl.register()

}
