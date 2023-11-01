package opensavvy.prepared.runner.kotlin

import opensavvy.prepared.suite.SuiteDsl
import opensavvy.prepared.suite.TestDsl
import opensavvy.prepared.suite.config.TestConfig
import opensavvy.prepared.suite.config.plus
import opensavvy.prepared.suite.runTestDsl
import org.junit.jupiter.api.DynamicContainer
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.util.stream.Stream
import kotlin.coroutines.CoroutineContext

actual abstract class TestExecutor {

	actual open val config: TestConfig
		get() = TestConfig.Empty

	actual abstract fun SuiteDsl.register()

	@TestFactory
	fun suite(): Stream<out DynamicNode> {
		val suite = JvmSuiteDsl(config).apply { register() }

		return suite.nodes.stream()
	}

}

private class JvmSuiteDsl(val parentConfig: TestConfig) : SuiteDsl {
	val nodes = ArrayList<DynamicNode>()

	override fun suite(name: String, config: TestConfig, block: SuiteDsl.() -> Unit) {
		val child = JvmSuiteDsl(parentConfig + config).apply(block)

		nodes += DynamicContainer.dynamicContainer(name, child.nodes)
	}

	override fun test(name: String, context: CoroutineContext, config: TestConfig, block: suspend TestDsl.() -> Unit) {
		nodes += DynamicTest.dynamicTest(name) {
			runTestDsl(name, context, parentConfig + config, block)
		}
	}
}
