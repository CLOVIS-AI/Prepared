package opensavvy.prepared.compat.gradle

import opensavvy.prepared.compat.filesystem.div
import opensavvy.prepared.suite.Prepared
import java.nio.file.Path

/**
 * Represents a Gradle project.
 *
 * To access an instance of this type, use [Gradle.rootProject] or [Gradle.project].
 */
class Project internal constructor(
	private val build: Gradle,

	/**
	 * The subdirectory of [Gradle.dir] in which this project is located.
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * test("Print the project structure") {
	 *     println(gradle.dir())
	 *     println(gradle.project("foo").dir())
	 * }
	 * ```
	 */
	val dir: Prepared<Path>,
) {

	internal val dsl get() = build.dsl

	/**
	 * The `build` directory for this [Project].
	 */
	val buildDir get() = dir / "build"
}
