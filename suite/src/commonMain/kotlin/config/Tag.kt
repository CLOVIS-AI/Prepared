/*
 * Copyright (c) 2024-2025, OpenSavvy and contributors.
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
