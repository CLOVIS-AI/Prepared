package opensavvy.prepared.compat.parameterize

import com.benwoodworth.parameterize.parameterOf
import com.benwoodworth.parameterize.parameterize
import opensavvy.prepared.runner.testballoon.preparedSuite

val ParameterizeExamples by preparedSuite {
	parameterize {
		val text by parameterOf("123456", "hello64")
		val int by parameterOf(4, 6)

		test("Prepared $text $int") {
			check(int.toString() in text)
		}
	}
}
