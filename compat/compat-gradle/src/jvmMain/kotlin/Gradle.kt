package opensavvy.prepared.compat.gradle

import opensavvy.prepared.compat.filesystem.createRandomDirectory
import opensavvy.prepared.compat.filesystem.div
import opensavvy.prepared.suite.TestDsl
import org.gradle.testkit.runner.GradleRunner
import kotlin.io.path.ExperimentalPathApi

@OptIn(ExperimentalPathApi::class)
private val rootProjectDir by createRandomDirectory("gradle-testkit-")

/**
 * Control center for Gradle TestKit. See [gradle].
 */
class Gradle internal constructor(
	internal val dsl: TestDsl,
) {

	/**
	 * A temporary directory unique for each test, in which the Gradle files are created.
	 *
	 * ### Example
	 *
	 * Print the directory:
	 * ```kotlin
	 * test("In which directory does Gradle execute?") {
	 *     println(gradle.dir())
	 * }
	 * ```
	 */
	val dir get() = rootProjectDir

	/**
	 * Accessor for the files of the root project.
	 *
	 * ### Example
	 *
	 * Create the root `build.gradle.kts` file:
	 * ```kotlin
	 * test("Create the root build.gradle.kts file") {
	 *     gradle.rootProject.buildKts("""
	 *         println("Configuring the project")
	 *     """.trimIndent()
	 * }
	 * ```
	 *
	 * @see project Access another project
	 * @see runner Execute the test and check the outputs
	 */
	val rootProject get() = Project(this, dir)

	/**
	 * Accessor for the files of a project, given its [path].
	 *
	 * ### Example
	 *
	 * Create the `modules/foo/build.gradle` file:
	 * ```kotlin
	 * test("Configure the :modules:foo project") {
	 *     gradle.project("modules/foo").buildGroovy("""
	 *         println "Configuring the project!"
	 *     """.trimIndent())
	 * }
	 * ```
	 *
	 * @see rootProject Access the root project
	 * @see runner Execute the test and check the outputs
	 */
	fun project(path: String) = Project(this, dir / path)

	/**
	 * Instantiates a [GradleRunner] in [dir].
	 *
	 * ### Examples
	 *
	 * ```kotlin
	 * test("Create the root build.gradle.kts file") {
	 *     gradle.rootProject.buildKts("""
	 *         tasks.register("print") {
	 *             doLast {
	 *                 println("Configuring the project")
	 *             }
	 *         }
	 *     """.trimIndent()
	 *
	 *     val result = gradle.runner()
	 *         .withPluginClasspath()
	 *         .withArguments("print")
	 *         .build()
	 *
	 *     result.output shouldContain "Configuring the project"
	 * }
	 * ```
	 *
	 * @see GradleRunner.withPluginClasspath When writing tests for a plugin, automatically adds it to the executed Gradle instance
	 * @see GradleRunner.withArguments Specify which tasks should be executed
	 * @see GradleRunner.build Executes the build, expecting a success
	 * @see GradleRunner.buildAndFail Executes the build, expecting a failure
	 */
	suspend fun runner(): GradleRunner = with(dsl) {
		GradleRunner.create()
			.withProjectDir(dir().toFile())
	}
}

/**
 * Control center for [Gradle TestKit](https://docs.gradle.org/current/userguide/test_kit.html).
 *
 * Each test is assigned a temporary directory in which Gradle can be configured (see [dir][Gradle.dir]).
 * Usually, tests will:
 * 1. Create the settings file (see [settingsGroovy] or [settingsKts]),
 * 2. Create the build script files (see [buildGroovy] or [buildKts]),
 * 3. Execute Gradle (see [runner][Gradle.runner])
 * 4. Make assertions on the result.
 */
val TestDsl.gradle: Gradle
	get() = Gradle(this)
