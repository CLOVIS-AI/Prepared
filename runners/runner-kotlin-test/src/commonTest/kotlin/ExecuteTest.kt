package opensavvy.prepared.runner.kotlin

import opensavvy.prepared.suite.SuiteDsl
import opensavvy.prepared.suite.cleanUp
import opensavvy.prepared.suite.config.Ignored
import opensavvy.prepared.suite.prepared
import kotlin.random.Random

@Suppress("unused")
class ExecuteTest : TestExecutor() {
	override fun SuiteDsl.register() {
		test("A simple test") {
			println("This test is declared with the Prepared syntax")
		}

		val factor = prepared("A randomized factor") {
			Random.nextInt()
		}

		val integer by prepared {
			cleanUp("Cleaning the prepared integer") { println("Cleaning up the integer…") }

			Random.nextInt() * factor()
		}

		suite("Group of tests") {
			test("Test 1") {
				integer()

				cleanUp("Stop the database") {
					println("Done")
				}

				println("It executes")
				println("Other line")
			}

			test("Test 2") {
				integer()
				println("It also executes")
			}
		}

		suite("Disabled suite", Ignored) {
			test("Always fails") {
				error("I should have been ignored")
			}
		}

		test("Disabled test", config = Ignored) {
			error("I should have been ignored")
		}
	}
}
