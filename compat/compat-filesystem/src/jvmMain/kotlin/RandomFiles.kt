package opensavvy.prepared.compat.filesystem

import opensavvy.prepared.suite.PreparedProvider
import opensavvy.prepared.suite.cleanUp
import opensavvy.prepared.suite.prepared
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.absolutePathString
import kotlin.io.path.deleteIfExists
import kotlin.io.path.deleteRecursively

/**
 * Creates a random directory, which is automatically deleted at the end of the test.
 *
 * ### Example
 *
 * ```kotlin
 * val buildDirectory by createRandomDirectory()
 *
 * test("A test") {
 *     buildDirectory() // do something with it
 * }
 * ```
 */
@ExperimentalPathApi
fun createRandomDirectory(prefix: String = ""): PreparedProvider<Path> = prepared {
	Files.createTempDirectory(prefix)
		.also { cleanUp("Delete the directory ${it.absolutePathString()}", onFailure = false) { it.deleteRecursively() } }
}

/**
 * Creates a random file, which is automatically deleted at the end of the test.
 *
 * ### Example
 *
 * ```kotlin
 * val logs by createRandomFile()
 *
 * test("A test") {
 *     logs().writeText("…")
 * }
 * ```
 */
fun createRandomFile(prefix: String = "", suffix: String = ""): PreparedProvider<Path> = prepared {
	Files.createTempFile(prefix, suffix)
		.also { cleanUp("Delete the file ${it.absolutePathString()}", onFailure = false) { it.deleteIfExists() } }
}
