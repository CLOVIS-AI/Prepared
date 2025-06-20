/*
 * Copyright (c) 2023-2025, OpenSavvy and contributors.
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
