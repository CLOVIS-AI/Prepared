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

interface TestDsl : PreparedDsl
