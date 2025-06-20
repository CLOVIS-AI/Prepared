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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import opensavvy.prepared.compat.kotlinx.datetime.now
import opensavvy.prepared.compat.kotlinx.datetime.set
import opensavvy.prepared.suite.*
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class)
class TimeTest : TestExecutor() {
	override fun SuiteDsl.register() {
		test("Elapsed time") {
			val start = time.source.markNow()
			delay(1000)
			val end = start.elapsedNow()

			assertEquals(1000, end.inWholeMilliseconds)
		}

		test("Current time with subtasks") {
			var executed = false

			launchInBackground {
				delay(1000)
				executed = true
			}

			launchInBackground {
				delay(1001)
				error("Never executed!")
			}

			assertEquals(false, executed)

			time.advanceByMillis(500)
			time.runCurrent()
			assertEquals(false, executed)

			time.advanceByMillis(500)
			time.runCurrent()
			assertEquals(true, executed)
		}

		test("Advance until idle") {
			launch {
				delay(1000)
				println("After 1 second")

				launch {
					delay(3000)
					println("After 4 seconds")
				}
			}

			launch {
				delay(2000)
				println("After 2 seconds")
			}

			time.advanceUntilIdle()
			assertEquals(4000, time.nowMillis)
		}

		test("Set the time") {
			time.set("2023-09-20T15:58:17.151Z")

			delay(1000)

			assertEquals("2023-09-20T15:58:18.151Z", time.now.toString())
		}
	}
}
