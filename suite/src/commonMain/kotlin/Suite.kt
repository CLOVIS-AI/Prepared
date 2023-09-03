package opensavvy.prepared.suite

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@DslMarker
annotation class PreparedDsl

@PreparedDsl
interface PreparedScope

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
interface Suite : PreparedScope {

	/**
	 * Creates a child suite named [name] of the current suite.
	 */
	@PreparedDsl
	fun suite(
		name: String,
		block: Suite.() -> Unit,
	)

	/**
	 * Declares a test named [name] as part of the current suite.
	 */
	@PreparedDsl
	fun test(
		name: String,
		context: CoroutineContext = EmptyCoroutineContext,
		block: suspend Test.() -> Unit,
	)

}

interface Test : PreparedScope
