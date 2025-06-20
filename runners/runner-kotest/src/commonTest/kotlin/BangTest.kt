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

class BangTest : PreparedSpec({

	test("!This test is skipped because of the bang") {
		error("This test starts with a bang, so it should not execute")
	}

	test("This test should run") {
		true shouldBe true
	}

	suite("!This suite is skipped") {
		test("This test shouldn't run") {
			error("This test should not execute due to the bang in the suite name")
		}
	}

	suite("This suite is not skipped") {
		test("This test is not skipped") {
			true shouldBe true
		}

		test("!This test is skipped") {
			error("This test should not execute due to the bang")
		}
	}

})
