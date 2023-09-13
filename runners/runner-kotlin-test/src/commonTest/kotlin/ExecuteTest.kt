package opensavvy.prepared.runner.kotlin

import opensavvy.prepared.suite.SuiteDsl

@Suppress("unused")
class ExecuteTest : TestExecutor() {
	override fun SuiteDsl.register() {
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
