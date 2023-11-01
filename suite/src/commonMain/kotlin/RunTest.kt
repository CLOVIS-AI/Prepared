package opensavvy.prepared.suite

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import opensavvy.prepared.suite.config.TestConfig
import kotlin.coroutines.CoroutineContext

private class TestDslImpl(
	override val environment: TestEnvironment,
) : TestDsl

/**
 * Low-level primitive to execute a test declared as a [TestDsl].
 *
 * Regular users of the library should never need to call this function.
 * It is only provided because it is required for people who implement their own test runner.
 */
fun runTestDsl(name: String, context: CoroutineContext, config: TestConfig, block: suspend TestDsl.() -> Unit): TestResult {
	return runTest(CoroutineName("Test ‘$name’") + context) {
		val test = TestDslImpl(
			environment = TestEnvironment(name, this),
		)

		try {
			test.block()
		} finally {
			with(test.environment.finalizers) { test.executeAllFinalizers() }
		}
	}
}
