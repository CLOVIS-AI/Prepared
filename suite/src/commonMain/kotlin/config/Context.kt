package opensavvy.prepared.suite.config

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Configures the [CoroutineContext] of the started tests.
 *
 * When a test is executed, the configured [context] is injected.
 * Note that the Prepared library injects some context elements itself, which will thus be ignored
 * even if set using this configuration.
 *
 * - The [CoroutineName] will be set to the test's name.
 * - The [CoroutineDispatcher] must not be changed, or it will break the [virtual time][opensavvy.prepared.suite.time].
 *
 * Because of this, this configuration is likely only useful to inject your own coroutine context elements, not
 * any of the built-ins.
 *
 * If multiple `Context` instances are placed in the same test config, the behavior is the same as when combining
 * multiple [CoroutineContext] elements: later elements of a given type override previous elements of the same type.
 *
 * ### Example
 *
 * Let's imagine that you have an `AuthContext` element that stores the current user, and is used throughout your
 * codebase to check access rights of the current user. You can use this configuration to inject it into all tests
 * that are part of suite:
 *
 * ```kotlin
 * suite("Suite name", Context(AuthContext.Guest)) {
 *     // …
 * }
 * ```
 *
 * Or within a specific test:
 * ```kotlin
 * test("As an admin, I want to do XXX", config = Context(AuthContext.Admin)) {
 *     // …
 * }
 * ```
 *
 * @see TestConfig.coroutineContext Access the configured elements.
 */
class Context(
	val context: CoroutineContext,
) : TestConfig.Element {

	override val key get() = Companion

	companion object : TestConfig.Key.Multi<Context>
}

/**
 * The coroutine context configured for this test through the [Context] configuration.
 *
 * Note that this is different from the coroutine context actually used to run tests; the value returned by this method
 * contains the context declared in the config, whereas more elements are injected by the test machinery when the test
 * actually starts.
 */
val TestConfig.coroutineContext: CoroutineContext
	get() = this[Context]
		.fold(EmptyCoroutineContext as CoroutineContext) { a, b -> a + b.context }
