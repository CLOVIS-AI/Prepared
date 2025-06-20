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

import opensavvy.prepared.runner.testballoon.preparedSuite
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

val RandomFilesTest by preparedSuite {
	testRandomFiles()
}
