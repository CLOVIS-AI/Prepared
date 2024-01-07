package opensavvy.prepared.compat.parameterize

import com.benwoodworth.parameterize.parameterOf
import com.benwoodworth.parameterize.parameterize
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import opensavvy.prepared.runner.kotest.preparedSuite
import opensavvy.prepared.suite.prepared

class ParamaterizeTests : StringSpec({
	preparedSuite {
		parameterize {
			val int by parameterOf(1, 2)

			test("Eager test for value $int") {
				(int > 0) shouldBe true
			}
		}

		parameterize {
			val first by prepared { 1.toString() }
			val second by prepared { 2.toString() }
			val int by parameterOf(first, second)

			test("Lazy test for value $int") {
				(int() in "123456789") shouldBe true
			}
		}
	}
})
