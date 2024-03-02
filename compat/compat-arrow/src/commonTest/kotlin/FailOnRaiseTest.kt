package opensavvy.prepared.compat.arrow.core

import arrow.core.raise.ExperimentalTraceApi
import io.kotest.assertions.throwables.shouldThrow
import opensavvy.prepared.runner.kotest.PreparedSpec

@Suppress("unused")
@OptIn(ExperimentalTraceApi::class)
class FailOnRaiseTest : PreparedSpec({
	test("Successful operation") {
		failOnRaise<Nothing, Int> { 5 }
	}

	test("Failed operation") {
		shouldThrow<AssertionError> {
			failOnRaise { raise(5) }
		}
	}
})
