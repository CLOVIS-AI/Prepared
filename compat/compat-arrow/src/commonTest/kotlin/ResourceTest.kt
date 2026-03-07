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

package opensavvy.prepared.compat.arrow

import arrow.fx.coroutines.resource
import kotlinx.coroutines.delay
import opensavvy.prepared.compat.arrow.coroutines.asPrepared
import opensavvy.prepared.compat.arrow.coroutines.install
import opensavvy.prepared.compat.arrow.coroutines.preparedResource
import opensavvy.prepared.runner.testballoon.preparedSuite
import kotlin.time.Duration.Companion.milliseconds

val ResourceTest by preparedSuite {

	test("Install during test") {
		var acquired = false
		var released = false
		var testEnded = false

		install(
			acquire = { acquired = true },
			release = { _, e ->
				released = true
				check(testEnded) { "The resource should only be released when the test ends, after all assertions" }
			},
		)

		check(acquired) { "The resource should be acquired immediately" }
		check(!released) { "The resource should only be released when the test ends, after all assertions" }
		testEnded = true
	}

	val integerResource = resource({
		delay(100.milliseconds)
		42
	}) { _, _ -> }

	test("Install an existing resource") {
		val resource = install(integerResource)

		check(resource == 42)
	}

	val prepareIntegerResource by integerResource.asPrepared()

	test("Convert an existing resource into a Prepared value") {
		val resource = prepareIntegerResource()

		check(resource == 42)
	}

	val prepareResources by preparedResource {
		integerResource.bind()
	}

	test("Convert an existing resource into a Prepared value") {
		val resource = prepareResources()

		check(resource == 42)
	}

}
