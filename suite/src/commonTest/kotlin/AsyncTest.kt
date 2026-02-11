/*
 * Copyright (c) 2025-2026, OpenSavvy and contributors.
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

package opensavvy.prepared.suite

import kotlinx.coroutines.delay
import opensavvy.prepared.runner.testballoon.preparedSuite
import opensavvy.prepared.suite.config.CoroutineTimeout
import kotlin.time.Duration.Companion.seconds

val AsyncTest by preparedSuite {
	test("empty test") {
		// This test purposefully left empty to initialize everything
		// The debug probes can take more than 3s to initialize, so without this test the next one would fail
		// with a timeout before it even starts.
	}

	test("A background task should not stop the test from finishing", CoroutineTimeout(1.seconds)) {
		println("foo")
		launchInBackground {
			// This coroutine should not block the end of the test.
			// It will be canceled at the end of the test automatically.
			delay(30.seconds)
		}
	}

	test("An exception in a background scope does not fail the test", CoroutineTimeout(1.seconds)) {
		println("foo")
		launchInBackground {
			error("Background task failed")
		}
	}
}
