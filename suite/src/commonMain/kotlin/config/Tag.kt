package opensavvy.prepared.suite.config

/**
 * Marks the test or an entire suite with a tag.
 *
 * Some test runners may allow the user to only execute tests with a specific tag, or ignore tasks with a specific tag.
 * A single test may be marked with multiple tags.
 *
 * ### Example
 *
 * Tag an entire suite:
 * ```kotlin
 * suite("Suite name", Tag("slow") + Tag("frontend")) {
 *     // …
 * }
 * ```
 *
 * Tag a specific test:
 * ```kotlin
 * test("Some kind of test", config = Tag("slow") + Tag("frontend")) {
 *     // …
 * }
 * ```
 */
data class Tag(
	val name: String,
) : TestConfig.Element {

	override val key get() = Companion

	companion object : TestConfig.Key.Multi<Tag>
}
