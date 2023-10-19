package opensavvy.prepared.runner.kotlin

import opensavvy.prepared.suite.SuiteDsl
import opensavvy.prepared.suite.randomInt
import kotlin.test.assertNotEquals

class RandomTest : TestExecutor() {

	override fun SuiteDsl.register() {
		val int1 by randomInt()
		val int2 by randomInt()

		test("A test with two random values") {
			assertNotEquals(int1(), int2())
		}
	}
}
