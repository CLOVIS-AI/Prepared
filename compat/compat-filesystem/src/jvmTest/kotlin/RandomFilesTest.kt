package opensavvy.prepared.compat.filesystem

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
