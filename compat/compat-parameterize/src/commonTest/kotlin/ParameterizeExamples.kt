package opensavvy.prepared.compat.parameterize

import com.benwoodworth.parameterize.parameterOf
import com.benwoodworth.parameterize.parameterize
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import opensavvy.prepared.runner.kotest.preparedSuite

class ParameterizeExamples : StringSpec({
	// Regular Kotest example
	parameterize {
		val text by parameterOf("123456", "hello64")
		val int by parameterOf(4, 6)

		"Kotest $text $int" {
			(int.toString() in text) shouldBe true
		}
	}

	// The same example with Prepared syntax
	preparedSuite {
		parameterize {
			val text by parameterOf("123456", "hello64")
			val int by parameterOf(4, 6)

			test("Prepared $text $int") {
				(int.toString() in text) shouldBe true
			}
		}
	}
})
