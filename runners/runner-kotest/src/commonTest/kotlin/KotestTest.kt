package opensavvy.prepared.runner.kotest

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay

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
	}
})
