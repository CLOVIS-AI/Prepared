package opensavvy.prepared.runner.kotlin

import opensavvy.prepared.suite.SuiteDsl
import opensavvy.prepared.suite.TestDsl
import opensavvy.prepared.suite.runTestDsl
import org.junit.jupiter.api.DynamicContainer
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.util.stream.Stream
import kotlin.coroutines.CoroutineContext

actual abstract class TestExecutor {

	actual abstract fun SuiteDsl.register()

	@TestFactory
	fun suite(): Stream<out DynamicNode> {
		val suite = JvmSuiteDsl().apply { register() }

		return suite.nodes.stream()
	}

}

private class JvmSuiteDsl : SuiteDsl {
	val nodes = ArrayList<DynamicNode>()

	override fun suite(name: String, block: SuiteDsl.() -> Unit) {
		val child = JvmSuiteDsl().apply(block)

		nodes += DynamicContainer.dynamicContainer(name, child.nodes)
	}

	override fun test(name: String, context: CoroutineContext, block: suspend TestDsl.() -> Unit) {
		nodes += DynamicTest.dynamicTest(name) {
			runTestDsl(name, context, block)
		}
	}
}
