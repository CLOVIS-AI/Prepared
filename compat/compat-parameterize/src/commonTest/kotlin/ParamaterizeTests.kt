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

package opensavvy.prepared.compat.parameterize

import com.benwoodworth.parameterize.ExperimentalParameterizeApi
import com.benwoodworth.parameterize.parameterOf
import com.benwoodworth.parameterize.parameterize
import kotlinx.coroutines.ExperimentalCoroutinesApi
import opensavvy.prepared.runner.testballoon.preparedSuite
import opensavvy.prepared.suite.*
import opensavvy.prepared.suite.random.nextInt
import opensavvy.prepared.suite.random.random
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@OptIn(ExperimentalParameterizeApi::class)
val ParamaterizeTests by preparedSuite {
	parameterize {
		val int by parameterOf(1, 2)

		test("Eager test for value $int") {
			check(int > 0)
		}
	}

	parameterize {
		val int by parameterOf(1, 2)
			.prepare { it.toString() }

		test("Lazy test for value $int") {
			check(int() in "123456789")
		}
	}

	parameterize {
		val currentTime by parameterOf("2022-12-31T23:37:00Z", "2000-01-01T23:37:00Z")
			.prepare { time.set(it) }

		val seed by parameterOf(0L, 13, -1)
			.prepare { random.setSeed(it) }

		val configure by prepared {
			currentTime()
			seed()
		}

		test("Test at $currentTime") {
			configure()

			time.advanceBy(5.minutes)
			println(time.now)

			println(random.nextInt())
		}
	}
}
