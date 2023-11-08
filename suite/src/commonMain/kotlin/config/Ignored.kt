package opensavvy.prepared.suite.config

/**
 * Marks a test or an entire suite as disabled.
 *
 * ### Example
 *
 * Mark a suite as disabled:
 * ```kotlin
 * suite(Ignored) {
 *     // …
 * }
 * ```
 *
 * Mark a test as disabled:
 * ```kotlin
 * test("Some kind of test", config = Ignored) {
 *     // …
 * }
 * ```
 */
object Ignored : TestConfig.Element, TestConfig.Key<Ignored> {
	override val key: TestConfig.Key<*>
		get() = this
}