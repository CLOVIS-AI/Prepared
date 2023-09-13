package opensavvy.prepared.runner.kotlin

import opensavvy.prepared.suite.SuiteDsl
import opensavvy.prepared.suite.cleanUp

@Suppress("unused")
class ExecuteTest : TestExecutor() {
	override fun SuiteDsl.register() {
		test("A simple test") {
			println("This test is declared with the Prepared syntax")
		}

		suite("Group of tests") {
			test("Test 1") {
				cleanUp("Stop the database") {
					println("Done")
				}

				println("It executes")
				println("Other line")
			}

			test("Test 2") {
				println("It also executes")
			}
		}
	}
}
