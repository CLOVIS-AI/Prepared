/*
 * Copyright (c) 2026, OpenSavvy and contributors.
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

import opensavvy.prepared.runner.testballoon.preparedSuite

val SimpleTest by preparedSuite {

	test("Basic Gradle task test") {
		gradle.settingsKts("""
			rootProject.name = "foo"
		""".trimIndent())

		gradle.properties("""
			myProperty='Hi, world'
		""".trimIndent())

		gradle.rootProject.buildKts("""
			val myProperty = providers.gradleProperty("myProperty")
			
			val print by tasks.registering {
				doLast {
					println(myProperty.get())
				}
			}
		""".trimIndent())

		val result = gradle.runner()
			.withArguments(":print")
			.withGradleVersion("9.2.0")
			.build()

		check("Hi, world" in result.output)
	}
}
