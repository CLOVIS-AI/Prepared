/*
 * Copyright (c) 2023-2026, OpenSavvy and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package opensavvy.prepared.compat.gradle

import opensavvy.prepared.compat.filesystem.div
import org.intellij.lang.annotations.Language
import kotlin.io.path.createDirectories
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
 * Accessor for the `gradle.properties` file.
 *
 * ### Example
 *
 * ```kotlin
 * test("Access the gradle.properties file") {
 *     println(gradle.properties())
 * }
 * ```
 */
val Gradle.properties get() = dir / "gradle.properties"

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
suspend fun Gradle.settingsGroovy(text: String) = with(dsl) {
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
suspend fun Gradle.settingsKts(text: String) = with(dsl) {
	settingsKts().writeText(text)
}

/**
 * Helper function to write the `gradle.properties` file.
 *
 * ### Example
 *
 * ```kotlin
 * test("Configure the JVM heap") {
 *     gradle.properties("""
 *         org.gradle.jvmargs=-Xmx3g -Xms200m
 *     """.trimIndent())
 * }
 * ```
 */
suspend fun Gradle.properties(@Language("properties") text: String) = with(dsl) {
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
suspend fun Project.buildGroovy(text: String) = with(dsl) {
	buildGroovy().parent.createDirectories()
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
suspend fun Project.buildKts(text: String) = with(dsl) {
	buildKts().parent.createDirectories()
	buildKts().writeText(text)
}

// endregion
