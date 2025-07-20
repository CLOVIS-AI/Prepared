/*
 * Copyright (c) 2025, OpenSavvy and contributors.
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

package opensavvy.prepared.compat.arrow.core

import opensavvy.prepared.runner.testballoon.preparedSuite
import opensavvy.prepared.suite.assertions.checkThrows
import opensavvy.prepared.suite.assertions.matches

val CheckRaisesTest by preparedSuite {

	test("Captures a raised value") {
		checkRaises<Int> {
			raise(5)
		}
	}

	test("Throws when nothing is raised") {
		val e = checkThrows<AssertionError> {
			checkRaises<Any?> {
				5
			}
		}
		check(e.message matches "Expected to fail with .*Any, but the operation was successful and returned 5")
	}

	test("Throws when the wrong thing is raised") {
		val e = checkThrows<AssertionError> {
			checkRaises(5) {
				raise(6)
			}
		}
		check(e.message matches "Expected to fail with 5, but failed with 6")
	}

	test("Throws when nothing is raised") {
		val e = checkThrows<AssertionError> {
			assertRaisesWith<Any?> {
				5
			}
		}
		check(e.message matches "Expected to fail with .*Any, but the operation was successful and returned 5")
	}

	test("Throws when the wrong thing is raised") {
		val e = checkThrows<AssertionError> {
			assertRaises(5) {
				raise(6)
			}
		}
		check(e.message matches "Expected to fail with 5, but failed with 6")
	}

}
