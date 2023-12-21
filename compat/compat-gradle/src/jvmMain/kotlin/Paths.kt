package opensavvy.prepared.compat.gradle

import opensavvy.prepared.compat.filesystem.div
import org.intellij.lang.annotations.Language
import kotlin.io.path.writeText

// region Build

/**
 * Accessor for the `settings.gradle` file.
 *
 * ### Example
 *
 * ```kotlin
 * test("Access the groovy settings file") {
 *     println(gradle.settingsGroovy())
 * }
 * ```
 *
 * @see settingsKts Kotlin equivalent
 * @see buildGroovy Build script file
 */
val Gradle.settingsGroovy get() = dir / "settings.gradle"

/**
 * Accessor for the `settings.gradle.kts` file.
 *
 * ### Example
 *
 * ```kotlin
 * test("Access the Kotlin DSL settings file") {
 *     println(gradle.settingsKts())
 * }
 * ```
 *
 * @see settingsGroovy Groovy equivalent
 * @see buildKts Build script file
 */
val Gradle.settingsKts get() = dir / "settings.gradle.kts"

/**
 * Helper function to write the `settings.gradle` file.
 *
 * ### Example
 *
 * ```kotlin
 * test("Create a groovy settings file") {
 *     gradle.settingsGroovy("""
 *         println "Loading the settings…"
 *     """.trimIndent())
 * }
 * ```
 *
 * @see settingsKts Kotlin equivalent
 * @see buildGroovy Build script file
 */
suspend fun Gradle.settingsGroovy(@Language("groovy") text: String) = with(dsl) {
	settingsGroovy().writeText(text)
}

/**
 * Helper function to write the `settings.gradle.kts` file.
 *
 * ### Example
 *
 * ```kotlin
 * test("Create a Kotlin DSL settings file") {
 *     gradle.settingsKts("""
 *         println("Loading the settings…")
 *     """.trimIndent())
 * }
 * ```
 *
 * @see settingsGroovy Groovy equivalent
 * @see buildKts Build script file
 */
suspend fun Gradle.settingsKts(@Language("kts") text: String) = with(dsl) {
	settingsKts().writeText(text)
}

// endregion
// region Project

/**
 * Accessor for the `build.gradle` file.
 *
 * ### Example
 *
 * ```kotlin
 * test("Access the groovy build file") {
 *     println(gradle.project("foo").buildGroovy())
 * }
 * ```
 *
 * @see buildKts Kotlin equivalent
 * @see settingsGroovy Settings file
 * @see Gradle.project Select the project
 */
val Project.buildGroovy get() = dir / "build.gradle"

/**
 * Accessor for the `build.gradle.kts` file.
 *
 * ### Example
 *
 * ```kotlin
 * test("Access the Kotlin build file") {
 *     println(gradle.project("foo").buildKts())
 * }
 * ```
 *
 * @see buildGroovy Groovy equivalent
 * @see settingsKts Settings file
 * @see Gradle.project Select the project
 */
val Project.buildKts get() = dir / "build.gradle.kts"

/**
 * Helper function to write the `build.gradle` file.
 *
 * ### Example
 *
 * ```kotlin
 * test("Create a groovy build file") {
 *     gradle.project("foo").buildGroovy("""
 *         println "Loading the project :foo…"
 *     """.trimIndent())
 * }
 * ```
 *
 * @see buildKts Kotlin equivalent
 * @see settingsGroovy Settings file
 * @see Gradle.project Select the project
 */
suspend fun Project.buildGroovy(@Language("groovy") text: String) = with(dsl) {
	buildGroovy().writeText(text)
}

/**
 * Helper function to write the `build.gradle.kts` file.
 *
 * ### Example
 *
 * ```kotlin
 * test("Create a Kotlin build file") {
 *     gradle.project("foo").buildKts("""
 *         println("Loading the project :foo…")
 *     """.trimIndent())
 * }
 * ```
 *
 * @see buildGroovy Groovy equivalent
 * @see settingsKts Settings file
 * @see Gradle.project Select the project
 */
suspend fun Project.buildKts(@Language("kts") text: String) = with(dsl) {
	buildKts().writeText(text)
}

// endregion
