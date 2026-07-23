/*
 * Copyright (c) 2024-2026, OpenSavvy and contributors.
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

import io.kotest.common.KotestInternal
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.AbstractSpec
import io.kotest.core.spec.TestDefinition
import io.kotest.core.spec.TestDefinitionBuilder
import io.kotest.core.spec.style.TestRunnable
import io.kotest.core.test.AbstractTestScope
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType
import io.kotest.engine.coroutines.coroutineTestScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import opensavvy.prepared.runner.kotest.KotestSuiteDsl.Companion.suiteToTestDefinition
import opensavvy.prepared.runner.kotest.KotestSuiteDsl.Companion.testToTestDefinition
import opensavvy.prepared.suite.SuiteDsl
import opensavvy.prepared.suite.TestDsl
import opensavvy.prepared.suite.config.*
import opensavvy.prepared.suite.runTestDslSuspend
import kotlin.jvm.JvmName

/**
 * Declares a Prepared test suite using the Kotest framework.
 *
 * Install the [Kotest IntelliJ plugin](https://kotest.io/docs/intellij/intellij-plugin.html) to have
 * gutter icons to run a specific test.
 *
 * ### Example
 *
 * ```kotlin
 * class MyTest : PreparedSpec({
 *     test("A regular test") {
 *         // …
 *     }
 *
 *     suite("A suite") {
 *         test("Another test") {
 *             // …
 *         }
 *     }
 * })
 * ```
 */
abstract class PreparedSpec(
	body: PreparedSpec.() -> Unit,
	private val config: TestConfig = TestConfig.Empty,
) : AbstractSpec(), SuiteDsl {

	init {
		body()
	}

	/**
	 * Creates a child suite named [name] of the current suite.
	 *
	 * ### Simple example
	 *
	 * ```kotlin
	 * suite("An example") {
	 *     test("A test") { … }
	 *
	 *     suite("A nested suite") {
	 *         test("A nested test 1") { … }
	 *         test("A nested test 2") { … }
	 *     }
	 * }
	 * ```
	 *
	 * ### Test configuration
	 *
	 * The default configuration for all tests can be passed with the [config] parameter:
	 *
	 * ```kotlin
	 * suite("An example", CoroutineTimeout(2.minutes) + Tag("slow")) {
	 *     …
	 * }
	 * ```
	 *
	 * To learn more about the available configuration options, see the subtypes of [TestConfig.Element].
	 */
	@TestRunnable
	@JvmName("suiteKotest")
	fun suite(
		name: String,
		config: TestConfig = TestConfig.Empty,
		block: KotestSuiteDsl.() -> Unit,
	) {
		add(
			suiteToTestDefinition(
				name = name,
				config = this@PreparedSpec.config + config,
				block = block,
			)
		)
	}

	// Necessary to override the interface but also force users to call the more specific overload
	// Only the more specific overload has IDE support
	@Deprecated("This method is not really deprecated, this is a trick to enhance the IDE experience", level = DeprecationLevel.HIDDEN)
	@TestRunnable
	override fun suite(
		name: String,
		config: TestConfig,
		block: SuiteDsl.() -> Unit,
	) {
		suite(name, config, block)
	}

	@TestRunnable
	override fun test(
		name: String,
		config: TestConfig,
		block: suspend TestDsl.() -> Unit,
	) {
		add(
			testToTestDefinition(
				name = name,
				config = this@PreparedSpec.config + config,
				block = block,
			)
		)
	}
}

/**
 * An implementation of Prepared's [SuiteDsl] that is recognized by the [Kotest IntelliJ plugin](https://kotest.io/docs/intellij/intellij-plugin.html).
 *
 * To create an instance of this class, use [PreparedSpec.suite] or [preparedSuite].
 */
class KotestSuiteDsl internal constructor(
	private val delegate: TestScope,
	private val parentConfig: TestConfig,
) : AbstractTestScope(delegate), SuiteDsl {

	/**
	 * Creates a child suite named [name] of the current suite.
	 *
	 * ### Simple example
	 *
	 * ```kotlin
	 * suite("An example") {
	 *     test("A test") { … }
	 *
	 *     suite("A nested suite") {
	 *         test("A nested test 1") { … }
	 *         test("A nested test 2") { … }
	 *     }
	 * }
	 * ```
	 *
	 * ### Test configuration
	 *
	 * The default configuration for all tests can be passed with the [config] parameter:
	 *
	 * ```kotlin
	 * suite("An example", CoroutineTimeout(2.minutes) + Tag("slow")) {
	 *     …
	 * }
	 * ```
	 *
	 * To learn more about the available configuration options, see the subtypes of [TestConfig.Element].
	 */
	@TestRunnable
	@JvmName("suiteKotest")
	fun suite(
		name: String,
		config: TestConfig = TestConfig.Empty,
		block: KotestSuiteDsl.() -> Unit,
	) {
		launch(Dispatchers.Unconfined) {
			registerTest(
				suiteToTestDefinition(
					name = name,
					config = this@KotestSuiteDsl.parentConfig + config,
					block = block,
				)
			)
		}
	}

	// Necessary to override the interface but also force users to call the more specific overload
	// Only the more specific overload has IDE support
	@Deprecated("This method is not really deprecated, this is a trick to enhance the IDE experience", level = DeprecationLevel.HIDDEN)
	@TestRunnable
	override fun suite(
		name: String,
		config: TestConfig,
		block: SuiteDsl.() -> Unit,
	) {
		suite(name, config, block)
	}

	@TestRunnable
	override fun test(
		name: String,
		config: TestConfig,
		block: suspend TestDsl.() -> Unit,
	) {
		launch(Dispatchers.Unconfined) {
			registerTest(
				testToTestDefinition(
					name = name,
					config = this@KotestSuiteDsl.parentConfig + config,
					block = block,
				)
			)
		}
	}

	@OptIn(KotestInternal::class)
	companion object {

		internal fun suiteToTestDefinition(
			name: String,
			config: TestConfig,
			block: KotestSuiteDsl.() -> Unit,
		): TestDefinition =
			TestDefinitionBuilder
				.builder(TestNameBuilder.builder(name).build(), TestType.Container)
				.build {
					KotestSuiteDsl(
						delegate = this,
						parentConfig = config,
					).block()
				}

		internal fun testToTestDefinition(
			name: String,
			config: TestConfig,
			block: suspend TestDsl.() -> Unit,
		): TestDefinition {
			val kotestConfig = io.kotest.core.test.config.TestConfig(
				enabled = config[Ignored] == null,
				tags = config[Tag]
					.mapTo(HashSet()) { io.kotest.core.Tag(it.name) },
				coroutineTestScope = true,
				coroutineDebugProbes = true,
			)

			return TestDefinitionBuilder
				.builder(TestNameBuilder.builder(name).build(), TestType.Test)
				.withConfig(kotestConfig)
				.build {
					coroutineTestScope.runTestDslSuspend(name, config, block)
				}
		}
	}
}
