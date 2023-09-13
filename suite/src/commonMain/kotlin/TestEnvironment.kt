package opensavvy.prepared.suite

import kotlinx.coroutines.test.TestScope

/**
 * Common utilities and features required for testing.
 */
class TestEnvironment internal constructor(
	/**
	 * The name of the test that is currently running.
	 */
	val testName: String,
	/**
	 * The [TestScope] this test runs in, used for time control and concurrent work.
	 */
	val coroutineScope: TestScope,
) {

	internal val cache = Cache()
	internal val finalizers = Finalizers()
}
