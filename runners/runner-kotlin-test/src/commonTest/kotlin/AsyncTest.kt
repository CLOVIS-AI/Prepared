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

package opensavvy.prepared.runner.kotlin

import kotlinx.coroutines.delay
import opensavvy.prepared.suite.SuiteDsl
import opensavvy.prepared.suite.launch
import opensavvy.prepared.suite.launchInBackground

class AsyncTest : TestExecutor() {
	override fun SuiteDsl.register() {
		test("Start a job and wait for it to finish") {
			launch {
				delay(1000)
				println("Done")
			}
		}

		test("Start a job, but do not wait for it") {
			launchInBackground {
				delay(1000)
				error("Not printed")
			}
		}
	}
}
