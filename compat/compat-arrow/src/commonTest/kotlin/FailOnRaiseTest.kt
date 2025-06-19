package opensavvy.prepared.compat.arrow.core

import arrow.core.raise.ExperimentalTraceApi
import opensavvy.prepared.runner.testballoon.preparedSuite

@Suppress("unused")
@OptIn(ExperimentalTraceApi::class)
val FailOnRaiseTest by preparedSuite {
	test("Successful operation") {
		failOnRaise<Nothing, Int> { 5 }
	}

	test("Failed operation") {
		try {
			failOnRaise { raise(5) }
			error("Should have thrown an AssertionError")
		} catch (e: AssertionError) {
		}
	}
}
