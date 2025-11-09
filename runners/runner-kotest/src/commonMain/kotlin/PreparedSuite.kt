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

package opensavvy.prepared.runner.kotest

import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.scopes.RootScope
import io.kotest.core.spec.style.scopes.addTest
import io.kotest.core.test.TestScope
import opensavvy.prepared.suite.SuiteDsl
import opensavvy.prepared.suite.TestDsl
import opensavvy.prepared.suite.config.*

/**
 * Executes a Prepared [SuiteDsl] in a Kotest suite.
 *
 * Since Kotest cannot represent nested non-suspending test suites, all nested suites are declared at the top-level
 * of the provided [RootScope]. The name of the suite is appended at the start of the tests, so the suite structure is not lost.
 *
 * ### Example
 *
 * This example uses [StringSpec], but tests can be registered using any spec.
 *
 * ```kotlin
 * class MyTests : StringSpec({
 *     "A regular Kotest test" {
 *         // …
 *     }
 *
 *     preparedSuite {
 *         test("A regular Prepared test") {
 *             // …
 *         }
 *
 *         suite("A regular Prepared test suite") {
 *             // …
 *         }
 *     }
 * })
 * ```
 */
@KotestTestScope
fun RootScope.preparedSuite(
	config: TestConfig = TestConfig.Empty,
	block: SuiteDsl.() -> Unit,
) {
	NonNestedSuite(this, config).block()
}

private class NonNestedSuite(private val root: RootScope, private val parentConfig: TestConfig, private val prefix: String? = null) : SuiteDsl {
	override fun suite(name: String, config: TestConfig, block: SuiteDsl.() -> Unit) {
		NonNestedSuite(root, parentConfig + config, prefix child name).block()
	}

	override fun test(name: String, config: TestConfig, block: suspend TestDsl.() -> Unit) {
		val thisConfig = parentConfig + config

		val kotestConfig = io.kotest.core.test.config.TestConfig(
			enabled = thisConfig[Ignored] == null,
			tags = config[Tag]
				.mapTo(HashSet()) { io.kotest.core.Tag(it.name) },
			coroutineTestScope = true,
			coroutineDebugProbes = true,
		)

		val testName = prefix child name

		root.addTest(
			testName = TestName(
				name = testName,
				focus = testName.startsWith(FOCUS_PREFIX),
				bang = testName.startsWith(BANG_PREFIX),
				prefix = null,
				suffix = null,
				defaultAffixes = false,
			),
			disabled = false,
			config = kotestConfig,
		) {
			executeTest(name, config, block)
		}
	}
}

// Workaround to avoid using the coroutine dispatcher on KJS.
// See https://gitlab.com/opensavvy/groundwork/prepared/-/issues/59
internal expect suspend fun TestScope.executeTest(name: String, config: TestConfig, block: suspend TestDsl.() -> Unit)

private const val FOCUS_PREFIX = "f:"
private const val BANG_PREFIX = "!"

/**
 * Appends [name] at the end of `this`, handling the case where `this` is `null`.
 */
private infix fun String?.child(name: String) =
	when {
		// See https://kotest.io/docs/framework/conditional/conditional-tests-with-focus-and-bang.html
		this != null && name.startsWith(FOCUS_PREFIX) -> "$FOCUS_PREFIX$this • $name"
		this != null && name.startsWith(BANG_PREFIX) -> "$BANG_PREFIX$this • $name"
		this != null -> "$this • $name"
		else -> name
	}
