/*
 * Copyright (c) 2023-2025, OpenSavvy and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
 *     companion object : TestConfig.Key.Unique<YourConfigurationOption>
 * }
 * ```
 */
sealed interface TestConfig {

	/**
	 * Identifier of an [Element].
	 *
	 * @param U See [uniqueness]. See also [Key.Unique] and [Key.Multi] as shorthands.
	 */
	interface Key<E : Element, U : Uniqueness> {

		/**
		 * Marker for the uniqueness of the elements denoted by this key.
		 *
		 * - If [Uniqueness.Unique] is used, a single element for a given key may be present.
		 * - If [Uniqueness.Multi] is used, multiple elements for the same key may be present.
		 */
		val uniqueness: U

		/**
		 * Shorthand for a [Key] with a [uniqueness] of [Unique].
		 */
		interface Unique<E : Element> : Key<E, Uniqueness.Unique> {
			override val uniqueness: Uniqueness.Unique
				get() = Uniqueness.Unique
		}

		/**
		 * Shorthand for a [Key] with a [uniqueness] of [Multi].
		 */
		interface Multi<E : Element> : Key<E, Uniqueness.Multi> {
			override val uniqueness: Uniqueness.Multi
				get() = Uniqueness.Multi
		}
	}

	/**
	 * An arbitrary configuration element.
	 *
	 * Elements are grouped by their [key].
	 * The number of elements allowed to exist in the config for a single [key] is controlled by the [Uniqueness].
	 * To access an element in a [TestConfig], see [get].
	 *
	 * Note that keys are not necessarily correlated with element types; an element type could split its instances
	 * between two different keys, in which case multiple elements of that type could be a part of the same config
	 * (but they would still differ by key).
	 */
	// U is unused because it's a phantom type
	interface Element : TestConfig {
		/**
		 * The identifier for this test configuration element.
		 */
		val key: Key<*, *>
	}

	/**
	 * Marker for the uniqueness of a [TestConfig.Element].
	 */
	sealed class Uniqueness {
		/**
		 * Only one [Element] may be present in the config for a given [Key].
		 */
		data object Unique : Uniqueness()

		/**
		 * Multiple [Element] instances may be present in the config for a given [Key].
		 */
		data object Multi : Uniqueness()
	}

	/**
	 * The empty [TestConfig], useful as a default parameter when no particular configuration is required.
	 */
	object Empty : TestConfig {
		override fun toString() = "TestConfig[]"
	}
}

private data class CombinedTestConfig(
	val data: Map<Key<*, *>, List<Element>>,
) : TestConfig {

	override fun toString() = data.values.joinToString(", ", prefix = "TestConfig[", postfix = "]")
}

/**
 * Finds the [Element] identified by [key] in the current [TestConfig].
 *
 * It's not possible for multiple elements to share a key marked with [Uniqueness.Unique], so this function can never return multiple results.
 * However, no elements may be identified by a [key], in which case `null` is returned.
 */
@Suppress("UNCHECKED_CAST")
operator fun <E : Element> TestConfig.get(key: Key<E, Uniqueness.Unique>): E? = when (this) {
	is Empty -> null
	is CombinedTestConfig -> data[key]?.firstOrNull() as E?
	is Element -> this.takeIf { this.key == key } as E?
}

/**
 * Finds the [elements][Element] identified by [key] in the current [TestConfig].
 *
 * Keys marked with [Uniqueness.Multi] allow multiple elements of the same type, so this function returns a list.
 * If no elements are found, the returned list is [empty][List.isEmpty].
 */
@Suppress("UNCHECKED_CAST")
operator fun <E : Element> TestConfig.get(key: Key<E, Uniqueness.Multi>): List<E> = when (this) {
	is Empty -> emptyList()
	is CombinedTestConfig -> data.getOrElse(key) { emptyList() } as List<E>
	is Element -> listOfNotNull(this.takeIf { this.key == key } as E?)
}

/**
 * Combines two [TestConfig] instances.
 *
 * The elements of [other] always override or combine with the elements with the same key from the receiver (depending on the [Key.uniqueness]).
 */
operator fun TestConfig.plus(other: TestConfig): TestConfig = when {
	this is Empty -> other
	other is Empty -> this
	else -> CombinedTestConfig(buildMap {
		putAll(this@plus.asMap())

		for ((key, elements) in other.asMap()) {
			when (key.uniqueness) {
				Uniqueness.Unique -> {
					// Sanity check, just in case
					check(elements.size == 1) { "Attempted to combine two TestConfig instances, but the 'other' instance has multiple elements for $key, even though it has a uniqueness of ${Uniqueness.Unique}: $elements" }

					// There can only be a single elements, so we just erase whatever was stored
					put(key, elements)
				}

				Uniqueness.Multi -> {
					put(key, getOrElse(key) { emptyList() } + elements)
				}
			}
		}
	})
}

private fun TestConfig.asMap(): Map<Key<*, *>, List<Element>> = when (this) {
	is Empty -> emptyMap()
	is Element -> mapOf(key to listOf(this))
	is CombinedTestConfig -> data
}
