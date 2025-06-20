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
