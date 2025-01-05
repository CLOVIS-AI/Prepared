package opensavvy.prepared.suite

import opensavvy.prepared.suite.config.Context
import opensavvy.prepared.suite.config.TestConfig
import opensavvy.prepared.suite.config.plus
import opensavvy.prepared.suite.random.randomInt
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@DslMarker
annotation class PreparedDslMarker

@PreparedDslMarker
interface PreparedDsl

/**
 * A group of tests.
 *
 * ### Example
 *
 * ```kotlin
 * suite("An example") {
 *     test("A test") {
 *         println("Execution")
 *     }
 *
 *     suite("A group of tests") {
 *         test("First test") {
 *             println("Execution")
 *         }
 *
 *         text("Second test") {
 *             println("Execution")
 *         }
 *     }
 * }
 * ```
 */
interface SuiteDsl : PreparedDsl {

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
	@PreparedDslMarker
	fun suite(
		name: String,
		config: TestConfig = TestConfig.Empty,
		block: SuiteDsl.() -> Unit,
	)

	/**
	 * Declares a test named [name] as part of the current suite.
	 *
	 * ### Simple example
	 *
	 * ```kotlin
	 * suite("An example") {
	 *     test("A test") {
	 *         delay(2.minutes)          // can suspend
	 *         time.advanceBy(2.minutes) // can control the virtual time
	 *         launchInBackground { … }  // can start background tasks
	 *         // …
	 *     }
	 * }
	 * ```
	 *
	 * To learn more about the functionality available within tests, see [TestDsl].
	 *
	 * ### Test configuration
	 *
	 * The test's configuration can be passed with the [config] parameter:
	 * ```kotlin
	 * test("An example", CoroutineTimeout(2.minutes) + Tag("slow")) {
	 *     // …
	 * }
	 * ```
	 *
	 * To learn more about the available configuration options, see the subtypes of [TestConfig.Element].
	 */
	@PreparedDslMarker
	fun test(
		name: String,
		config: TestConfig = TestConfig.Empty,
		block: suspend TestDsl.() -> Unit,
	)

	@Deprecated(
		"Prefer injecting the coroutine context using the `Context` test configuration.",
		ReplaceWith("test(name, config + Context(context), block)", "opensavvy.prepared.suite.config.*"),
		DeprecationLevel.WARNING
	)
	@PreparedDslMarker
	fun test(
		name: String,
		context: CoroutineContext = EmptyCoroutineContext,
		config: TestConfig = TestConfig.Empty,
		block: suspend TestDsl.() -> Unit,
	) = test(name, config + Context(context), block)

}

/**
 * A test declaration.
 *
 * This interface is most often used as a test declaration: `suspend TestDsl.() -> Unit`.
 *
 * Tests allow to control the time. For more information, read [time].
 *
 * ### Design notes
 *
 * It is our goal to keep this interface as lightweight as possible, because any field we add here risks being shadowed
 * by local variables in the tests.
 *
 * For example, if we were to add a member called `foo`, then this code…
 * ```kotlin
 * test("Test") {
 *     val foo = …
 * }
 * ```
 * …shadows the member 'foo'.
 *
 * Instead, we add all fields to [TestEnvironment], and create extension functions which expose the most important functionality.
 *
 * ### Note to runner implementors
 *
 * If you are implementing your own test runner, you will need to provide an instance of this interface.
 * Because it encapsulates the whole test machinery, we recommend using [runTestDsl] instead of making your own
 * implementation.
 *
 * @see cleanUp Register a finalizer which is executed at the end of the test
 */
interface TestDsl : PreparedDsl {

	/**
	 * Metadata about the running test.
	 */
	val environment: TestEnvironment

	/**
	 * Realizes a [Prepared] value in the context of this test.
	 */
	suspend operator fun <T> Prepared<T>.invoke(): T =
		executeIn(this@TestDsl)

	/**
	 * Realizes a [Prepared] value from the provided [PreparedProvider].
	 *
	 * Because the prepared value is created and used immediately, it cannot be saved to be reused—this means that it
	 * won't have the reuse behavior of [Prepared]; that is:
	 *
	 * ```kotlin
	 * val prepareRandomInt = prepared { random.nextInt() }
	 * val first by prepareRandomInt // bind to a Prepared instance
	 *
	 * test("An example") {
	 *     assertEquals(first(), first()) // it is bound, so it always gives the same value
	 *
	 *     assertNotEquals(prepareRandomInt.immediate(), prepareRandomInt.immediate()) // it is unbound, so each call gives a new value
	 * }
	 * ```
	 *
	 * This function is mostly useful because test fixtures are often provided as [PreparedProvider] instance to
	 * benefit from the other features of this library.
	 * Sometimes, however, we just need a single value at a single point in time, which is why this function exists.
	 */
	suspend fun <T> PreparedProvider<T>.immediate(name: String = "Immediate value #${randomInt()}"): T =
		named(name)()

}
