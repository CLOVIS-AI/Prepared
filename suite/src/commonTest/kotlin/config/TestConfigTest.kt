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

import opensavvy.prepared.runner.testballoon.preparedSuite

private data class UniqueConfig(
	val id: Int,
) : TestConfig.Element {

	override val key get() = Companion

	companion object : TestConfig.Key.Unique<UniqueConfig>
}

private data class MultiConfig(
	val id: Int,
) : TestConfig.Element {

	override val key get() = Companion

	companion object : TestConfig.Key.Multi<MultiConfig>
}

val TestConfigTest by preparedSuite {

	test("Combining multiple unique configurations only keeps the last one") {
		val config = UniqueConfig(43) + UniqueConfig(1)
		check(config[UniqueConfig] == UniqueConfig(1))
	}

	test("Combining multiple unique configurations only keeps the last one, even if it also contains a multi configuration") {
		val config = UniqueConfig(43) + MultiConfig(12) + UniqueConfig(1)
		check(config[UniqueConfig] == UniqueConfig(1))
	}

	test("Combining multiple multi configurations keeps them all in order") {
		val config = MultiConfig(43) + MultiConfig(1)
		check(config[MultiConfig] == listOf(MultiConfig(43), MultiConfig(1)))
	}

	test("Combining multiple multi configurations keeps them all in order, even if it also contains a single configuration") {
		val config = MultiConfig(43) + UniqueConfig(12) + MultiConfig(1)
		check(config[MultiConfig] == listOf(MultiConfig(43), MultiConfig(1)))
	}

}
