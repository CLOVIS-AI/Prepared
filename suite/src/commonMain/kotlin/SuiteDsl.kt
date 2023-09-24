package opensavvy.prepared.suite

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
	 */
	@PreparedDslMarker
	fun suite(
		name: String,
		block: SuiteDsl.() -> Unit,
	)

	/**
	 * Declares a test named [name] as part of the current suite.
	 */
	@PreparedDslMarker
	fun test(
		name: String,
		context: CoroutineContext = EmptyCoroutineContext,
		block: suspend TestDsl.() -> Unit,
	)

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
	suspend operator fun <T : Any> Prepared<T>.invoke(): T =
		executeIn(this@TestDsl)

}
