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

package opensavvy.prepared.runner.testballoon

import de.infix.testBalloon.framework.core.*
import de.infix.testBalloon.framework.shared.TestDisplayName
import de.infix.testBalloon.framework.shared.TestElementName
import de.infix.testBalloon.framework.shared.TestRegistering
import opensavvy.prepared.suite.PreparedDslMarker
import opensavvy.prepared.suite.SuiteDsl
import opensavvy.prepared.suite.TestDsl
import opensavvy.prepared.suite.config.*
import opensavvy.prepared.suite.config.TestConfig
import opensavvy.prepared.suite.runTestDslSuspend
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import de.infix.testBalloon.framework.core.TestConfig as BalloonTestConfig

@PreparedDslMarker
@TestRegistering
fun TestSuite.withPrepared(
	config: TestConfig = TestConfig.Empty,
	block: SuiteDsl.() -> Unit,
) {
	TestBalloonSuite(this, config).apply(block)
}

@PreparedDslMarker
@TestRegistering
fun preparedSuite(
	@TestElementName name: String = "",
	@TestDisplayName displayName: String = name,
	balloonConfig: BalloonTestConfig = BalloonTestConfig,
	preparedConfig: TestConfig = TestConfig.Empty,
	content: SuiteDsl.() -> Unit,
) = testSuite(name, displayName, balloonConfig) {
	withPrepared(preparedConfig) {
		content()
	}
}

private class TestBalloonSuite(
	private val upstream: TestSuite,
	private val config: TestConfig,
) : SuiteDsl {
	override fun suite(name: String, config: TestConfig, block: SuiteDsl.() -> Unit) {
		val effectiveConfig = this.config + config
		upstream.testSuite(name, testConfig = effectiveConfig.toBalloon()) {
			withPrepared(effectiveConfig) { block() }
		}
	}

	override fun test(name: String, config: TestConfig, block: suspend TestDsl.() -> Unit) {
		val effectiveConfig = this.config + config
		upstream.test(name, testConfig = effectiveConfig.toBalloon()) {
			this.testScope.runTestDslSuspend(name, effectiveConfig, block)
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
