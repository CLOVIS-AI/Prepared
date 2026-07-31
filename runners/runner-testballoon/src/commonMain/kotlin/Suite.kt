/*
 * Copyright (c) 2025-2026, OpenSavvy and contributors.
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

package opensavvy.prepared.runner.testballoon

import de.infix.testBalloon.framework.core.*
import de.infix.testBalloon.framework.shared.TestElementName
import de.infix.testBalloon.framework.shared.TestRegistering
import de.infix.testBalloon.framework.shared.TestSuitePropertyName
import opensavvy.prepared.suite.PreparedDslMarker
import opensavvy.prepared.suite.SuiteDsl
import opensavvy.prepared.suite.TestDsl
import opensavvy.prepared.suite.config.*
import opensavvy.prepared.suite.config.TestConfig
import opensavvy.prepared.suite.runTestDslSuspend
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.JvmName
import de.infix.testBalloon.framework.core.TestConfig as BalloonTestConfig

/**
 * Declares Prepared-style tests in an existing TestBalloon-style suite.
 *
 * To declare an entire suite with Prepared, see [preparedSuite].
 */
@Suppress("DSL_MARKER_APPLIED_TO_WRONG_TARGET")
@PreparedDslMarker
@TestRegistering
fun TestSuite.withPrepared(
	config: TestConfig = TestConfig.Empty,
	block: TestBalloonSuiteDsl.() -> Unit,
) {
	TestBalloonSuiteDsl(this, config).apply(block)
}

/**
 * Declares a top-level [suite](https://prepared.opensavvy.dev/tutorials/syntax.html) (a group of related tests).
 *
 * ### Example
 *
 * ```kotlin
 * val myTestSuite by preparedSuite {
 *     test("A simple test") {
 *         // …
 *     }
 *
 *     suite("A nested suite of tests") {
 *         test("A second test") {
 *             // …
 *         }
 *
 *         test("A third test") {
 *             // …
 *         }
 *     }
 * }
 * ```
 *
 * ### IDE support
 *
 * Tests declared with this DSL can be executed individually with the [TestBalloon IntelliJ plugin](https://plugins.jetbrains.com/plugin/27749-testballoon).
 */
@Suppress("DSL_MARKER_APPLIED_TO_WRONG_TARGET")
@PreparedDslMarker
@TestRegistering
fun preparedSuite(
	@TestElementName name: String? = null,
	balloonConfig: BalloonTestConfig = BalloonTestConfig,
	preparedConfig: TestConfig = TestConfig.Empty,
	compartment: () -> TestCompartment = { TestCompartment.Default },
	@TestSuitePropertyName qualifiedPropertyName: String = "",
	content: TestBalloonSuiteDsl.() -> Unit,
) = testSuite(name, compartment, balloonConfig, qualifiedPropertyName) {
	withPrepared(preparedConfig) {
		content()
	}
}

/**
 * An implementation of Prepared's [SuiteDsl] that is recognized by the [TestBalloon IntelliJ plugin](https://plugins.jetbrains.com/plugin/27749-testballoon).
 *
 * To create an instance of this class, use [preparedSuite] or [withPrepared].
 */
class TestBalloonSuiteDsl internal constructor(
	private val upstream: TestSuiteScope,
	private val config: TestConfig,
) : SuiteDsl {

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
	@TestRegistering
	@JvmName("suiteTestBalloon")
	fun suite(
		name: String,
		config: TestConfig = TestConfig.Empty,
		block: TestBalloonSuiteDsl.() -> Unit,
	) {
		val effectiveConfig = this.config + config

		with(upstream) {
			upstream.testSuite(name, testConfig = effectiveConfig.toBalloon()) {
				withPrepared(effectiveConfig) { block() }
			}
		}
	}

	// Necessary to override the interface but also force users to call the more specific overload
	// Only the more specific overload has IDE support
	@Deprecated("This method is not really deprecated, this is a trick to enhance the IDE experience", level = DeprecationLevel.HIDDEN)
	override fun suite(name: String, config: TestConfig, block: SuiteDsl.() -> Unit) {
		suite(name, config, block)
	}

	@TestRegistering
	override fun test(name: String, config: TestConfig, block: suspend TestDsl.() -> Unit) {
		val effectiveConfig = this.config + config

		with(upstream) {
			upstream.test(name, testConfig = effectiveConfig.toBalloon()) {
				this.testScope.runTestDslSuspend(name, effectiveConfig, block)
			}
		}
	}
}

private fun TestConfig.toBalloon(): BalloonTestConfig {
	var config = BalloonTestConfig
		.testScope(isEnabled = true, timeout = this[CoroutineTimeout]?.duration ?: CoroutineTimeout.Default)

	if (this[Ignored] != null)
		config = config.disable()

	if (this[Context].isNotEmpty())
		config = config.coroutineContext(this[Context].fold(EmptyCoroutineContext as CoroutineContext) { acc, it -> acc + it.context })

	return config
}
