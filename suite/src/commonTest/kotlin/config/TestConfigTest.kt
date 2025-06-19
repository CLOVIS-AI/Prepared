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
