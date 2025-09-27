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

package opensavvy.prepared.suite

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import opensavvy.prepared.suite.config.TestConfig
import opensavvy.prepared.suite.config.coroutineContext
import opensavvy.prepared.suite.config.effectiveTimeout

private class TestDslImpl(
	override val environment: TestEnvironment,
) : TestDsl

/**
 * Low-level primitive to execute a test declared as a [TestDsl].
 *
 * Regular users of the library should never need to call this function.
 * It is only provided because it is required for people who implement their own test runner.
 */
fun runTestDsl(name: String, config: TestConfig, block: suspend TestDsl.() -> Unit): TestResult {
	return runTest(timeout = config.effectiveTimeout()) {
		runTestDslSuspend(name, config, block)
	}
}

/**
 * Low-level primitive to execute a test declared as a [TestDsl], when already inside a [TestScope].
 *
 * Regular users of the library should never need to call this function. It is only provided because it is required
 * for people who implement their own test runner.
 */
suspend fun TestScope.runTestDslSuspend(name: String, config: TestConfig, block: suspend TestDsl.() -> Unit) {
	withContext(config.coroutineContext + CoroutineName("Test ‘$name’")) {
		val test = TestDslImpl(
			environment = TestEnvironment(name, this@runTestDslSuspend)
		)

		var successful = false
		try {
			test.platformSpecificFeatures(block)
			successful = true
		} catch (e: Throwable) {
			println()
			println("An ${e::class.simpleName ?: e::class} was thrown during the test. The details are displayed at the end of the test, possibly after the finalizers.")
			throw e
		} finally {
			with(test.environment.finalizers) { test.executeAllFinalizers(successful = successful) }
		}
	}
}

internal expect suspend inline fun TestDsl.platformSpecificFeatures(block: suspend TestDsl.() -> Unit)
