package opensavvy.prepared.compat.arrow.core

import arrow.core.raise.ExperimentalTraceApi
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import opensavvy.prepared.runner.kotest.preparedSuite

@OptIn(ExperimentalTraceApi::class)
class FailOnRaiseTest : StringSpec({
	preparedSuite {
		test("Successful operation") {
			failOnRaise<Nothing, Int> { 5 }
		}

		test("Failed operation") {
			shouldThrow<AssertionError> {
				failOnRaise { raise(5) }
			}
		}
	}
})
