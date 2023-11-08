package opensavvy.prepared.runner.kotlin

import kotlinx.coroutines.delay
import opensavvy.prepared.suite.SuiteDsl
import opensavvy.prepared.suite.cleanUp
import opensavvy.prepared.suite.config.Ignored
import opensavvy.prepared.suite.prepared
import opensavvy.prepared.suite.shared
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

		val longTask by shared {
			delay(10_000)
		}

		suite("Group of tests") {
			test("Test 1") {
				integer()
				longTask()

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

			test("Test 3") {
				longTask()
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
