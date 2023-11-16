package opensavvy.prepared.runner.kotest

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import opensavvy.prepared.suite.config.Ignored

class KotestTest : StringSpec({
	// Vanilla Kotest declarations…
	"Kotest" {
		println("Done")
	}

	"Hello world from Kotest" {
		"hello".length shouldBe 5
	}

	// Prepared integration…
	preparedSuite {
		suite("Prepared") {
			suite("Nested") {
				test("Prepared") {
					delay(1000)
					println("Done")
				}

				test("Other") {
					println("Nothing to do")
				}
			}
		}

		test("Hello world from Prepared") {
			"hello".length shouldBe 5
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
})
