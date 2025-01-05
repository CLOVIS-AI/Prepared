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
			test.block()
			successful = true
		} finally {
			with(test.environment.finalizers) { test.executeAllFinalizers(successful = successful) }
		}
	}
}
