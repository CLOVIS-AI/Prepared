package opensavvy.prepared.runner.kotlin

import opensavvy.prepared.suite.SuiteDsl

/**
 * Entrypoint to declare a [SuiteDsl] executed with [kotlin-test](https://kotlinlang.org/api/latest/kotlin.test/).
 *
 * Because `kotlin-test` doesn't provide a way to dynamically instantiate tests, we have to cheat.
 * This class abstracts away our hacks to make it work.
 * Please vote on [KT-46899](https://youtrack.jetbrains.com/issue/KT-46899).
 *
 * Because of these hacks, implementing this class has strange requirements:
 * - an implementation class's name must contain `Test`
 *
 * ### Example
 *
 * ```kotlin
 * class ExecuteTest : TestExecutor() {
 *     override fun Suite.register() {
 *         test("This is a test") {
 *             println("Hello world!")
 *         }
 *     }
 * }
 * ```
 */
expect abstract class TestExecutor() {

	/**
	 * Declares a [SuiteDsl] which will be run with `kotlin-test`.
	 *
	 * For more information, see the [class-level documentation][TestExecutor].
	 */
	abstract fun SuiteDsl.register()

}
