package opensavvy.prepared.runner.kotlin

import kotlinx.coroutines.test.runTest
import opensavvy.prepared.suite.Suite
import opensavvy.prepared.suite.Test
import org.junit.jupiter.api.DynamicContainer
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.util.stream.Stream
import kotlin.coroutines.CoroutineContext

actual abstract class TestExecutor {

	actual abstract fun Suite.register()

	@TestFactory
	fun suite(): Stream<out DynamicNode> {
		val suite = JvmSuite().apply { register() }

		return suite.nodes.stream()
	}

}

private class JvmSuite : Suite {
	val nodes = ArrayList<DynamicNode>()

	override fun suite(name: String, block: Suite.() -> Unit) {
		val child = JvmSuite().apply(block)

		nodes += DynamicContainer.dynamicContainer(name, child.nodes)
	}

	override fun test(name: String, context: CoroutineContext, block: suspend Test.() -> Unit) {
		nodes += DynamicTest.dynamicTest(name) {
			runTest(context) { block(JvmTest) }
		}
	}
}

private object JvmTest : Test
