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

package opensavvy.prepared.runner.kotest

import io.kotest.matchers.shouldBe

class FocusTest : PreparedSpec({

	test("f:This test is focused, and should run") {
		true shouldBe true
	}

	test("This test is not focused, and should not run") {
		error("This test doesn't start with 'f:', meaning it shouldn't run")
	}

	suite("f:This suite is focused, and should run") {
		test("First test") {
			true shouldBe true
		}

		test("Second test") {
			true shouldBe true
		}
	}

	suite("This suite is not focused, and should not run") {
		test("This test is inside a non-focused test, and should therefore not run due to the top-level focused test") {
			error("A top-level is focused, and this test isn't, and its suite isn't, so it shouldn't run")
		}

		test("f:This test is focused") {
			true shouldBe true
		}
	}

})
