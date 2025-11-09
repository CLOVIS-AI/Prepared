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

package opensavvy.prepared.runner.kotest

import io.kotest.core.spec.style.StringSpec
import kotlinx.coroutines.delay
import opensavvy.prepared.suite.config.Ignored

class KotestTest : StringSpec({
	// Vanilla Kotest declarations…
	"Kotest" {
		println("Done")
	}

	"Hello world from Kotest" {
		check("hello".length == 5)
	}

	// Prepared integration…
	preparedSuite {
		suite("Prepared") {
			suite("Nested") {
				test("Prepared") {
					delay(1000)
					println("Done")
				}

				test("Other") {
					println("Nothing to do")
				}
			}
		}

		test("Hello world from Prepared") {
			check("hello".length == 5)
		}

		suite("Disabled suite", Ignored) {
			test("Always fails") {
				error("I should have been ignored")
			}
		}

		test("Disabled test", config = Ignored) {
			error("I should have been ignored")
		}
	}
})

class KotestTest2 : PreparedSpec({
	suite("Test") {
		test("Test") {
			check(true)
		}
	}
})
