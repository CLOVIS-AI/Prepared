package opensavvy.prepared.suite.config

import opensavvy.prepared.suite.config.TestConfig.*

/**
 * Generic configuration description.
 *
 * Configuration items are subclasses of [Element], each identified by a unique [Key].
 * Complex configurations are created by combining simpler items using [plus].
 * Accessing a specific item is done using [get].
 *
 * ### How to create a new configuration option
 *
 * Create a class that subclasses [Element] with a companion object that subclasses [Key].
 * You can store any data in this class.
 *
 * ```kotlin
 * class YourConfigurationOption : TestConfig.Element {
 *     override val key get() = Companion
 *
 *     companion object : TestConfig.Key
 * }
 * ```
 */
sealed interface TestConfig {

	/**
	 * Identifier of an [Element].
	 */
	interface Key<E : Element>

	/**
	 * An arbitrary configuration element.
	 *
	 * Elements are grouped by their [key]: in a given [TestConfig], at most one element of a given key can be present.
	 * To access an element in a [TestConfig], see [get].
	 *
	 * Note that keys are not necessarily correlated with element types; an element type could split its instances
	 * between two different keys, in which case multiple elements of that type could be a part of the same config
	 * (but they would still differ by key).
	 */
	interface Element : TestConfig {
		val key: Key<*>
	}

	/**
	 * The empty [TestConfig], useful as a default parameter when no particular configuration is required.
	 */
	object Empty : TestConfig {
		override fun toString() = "TestConfig[]"
	}
}

private data class CombinedTestConfig(
	val data: Map<Key<*>, TestConfig>,
) : TestConfig {

	override fun toString() = data.values.joinToString(", ", prefix = "TestConfig[", postfix = "]")
}

/**
 * Finds the [Element] identified by [key] in the current [TestConfig].
 *
 * It's not possible for multiple elements to share the same key, so this function can never return multiple results.
 * However, no elements may be identified by a [key], in which case `null` is returned.
 */
@Suppress("UNCHECKED_CAST")
operator fun <E : Element> TestConfig.get(key: Key<E>): E? = when (this) {
	is Empty -> null
	is CombinedTestConfig -> data[key] as E?
	is Element -> this.takeIf { this.key == key } as E?
}

/**
 * Combines two [TestConfig] instances.
 *
 * The elements of [other] always override the elements with the same key from the receiver.
 */
operator fun TestConfig.plus(other: TestConfig): TestConfig = when {
	this is Empty -> other
	other is Empty -> this
	else -> CombinedTestConfig(this.asMap() + other.asMap())
}

private fun TestConfig.asMap(): Map<Key<*>, TestConfig> = when (this) {
	is Empty -> emptyMap()
	is Element -> mapOf(key to this)
	is CombinedTestConfig -> data
}
