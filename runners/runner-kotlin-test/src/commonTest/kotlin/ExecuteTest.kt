package opensavvy.prepared.runner.kotlin

import opensavvy.prepared.suite.Suite

@Suppress("unused")
class ExecuteTest : TestExecutor() {
	override fun Suite.register() {
		test("A simple test") {
			println("This test is declared with the Prepared syntax")
		}

		suite("Group of tests") {
			test("Test 1") {
				println("It executes")
				println("Other line")
			}

			test("Test 2") {
				println("It also executes")
			}
		}
	}
}
