package opensavvy.prepared.runner.kotlin

import kotlinx.coroutines.delay
import opensavvy.prepared.suite.SuiteDsl
import opensavvy.prepared.suite.launch
import opensavvy.prepared.suite.launchInBackground

class AsyncTest : TestExecutor() {
	override fun SuiteDsl.register() {
		test("Start a job and wait for it to finish") {
			launch {
				delay(1000)
				println("Done")
			}
		}

		test("Start a job, but do not wait for it") {
			launchInBackground {
				delay(1000)
				error("Not printed")
			}
		}
	}
}
