/*
 * Copyright (c) 2024-2025, OpenSavvy and contributors.
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

import opensavvy.prepared.compat.filesystem.pkg.PkgPackage
import opensavvy.prepared.compat.filesystem.resources.ExperimentalResourceApi
import opensavvy.prepared.compat.filesystem.resources.resource
import opensavvy.prepared.runner.testballoon.preparedSuite

private object ResourcesTestClass

@OptIn(ExperimentalResourceApi::class)
val ResourcesTest by preparedSuite {

	suite("Read from different paths") {
		val fromCurrentClass by resource<ResourcesTestClass>("resource-text.txt")
			.read()

		val fromSubPackage by resource<PkgPackage>("test.txt")
			.read()

		val fromClassLoader by resource("root.txt", PkgPackage::class.java.classLoader)
			.read()

		test("Load resource from current package") {
			check(fromCurrentClass() == "From current package\n")
		}

		test("Load resource from another package") {
			check(fromSubPackage() == "From pkg package\n")
		}

		test("Load resource from root class loader") {
			check(fromClassLoader() == "From root\n")
		}
	}

}
