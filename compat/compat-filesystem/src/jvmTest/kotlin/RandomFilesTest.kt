package opensavvy.prepared.compat.filesystem

import io.kotest.core.spec.style.StringSpec
import opensavvy.prepared.runner.kotest.preparedSuite
import opensavvy.prepared.suite.SuiteDsl
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.writeText

@OptIn(ExperimentalPathApi::class)
fun SuiteDsl.testRandomFiles() = suite("Random files") {
	val directory by createRandomDirectory()
	val readme = directory / "README.md"

	test("First test") {
		readme().writeText("This is a test")
	}
}

class RandomFilesTest : StringSpec({
	preparedSuite { testRandomFiles() }
})
