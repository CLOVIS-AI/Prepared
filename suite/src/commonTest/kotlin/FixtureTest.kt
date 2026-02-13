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

package opensavvy.prepared.suite

import opensavvy.prepared.runner.testballoon.preparedSuite
import opensavvy.prepared.suite.assertions.checkThrows

val FixtureTest by preparedSuite {

	fun Throwable.rootCause(): Throwable =
		this.cause?.rootCause() ?: this

	test("Exception in prepared value") {
		val willThrow by prepared {
			throw IllegalStateException("foo")
		}

		val e = checkThrows<AssertionError> {
			willThrow()
		}
		check(e.message == "An exception was thrown while computing the prepared value ‘willThrow’")
		val cause = e.rootCause()
		check(cause is IllegalStateException && cause.message == "foo")
	}

	test("A failed prepared value should always return the same exception instance within the same test") {
		val willThrow by prepared {
			throw IllegalStateException("foo")
		}

		val a = checkThrows<AssertionError> {
			willThrow()
		}

		val b = checkThrows<AssertionError> {
			willThrow()
		}

		check(a === b)
	}

	test("Exception in shared value") {
		val willThrow by shared {
			throw IllegalStateException("foo")
		}

		val e = checkThrows<AssertionError> {
			willThrow()
		}
		check(e.message == "An exception was thrown while computing the shared value ‘willThrow’")
		val cause = e.rootCause()
		check(cause is IllegalStateException && cause.message == "foo")
	}

	test("A failed shared value should always return the same exception instance") {
		val willThrow by shared {
			throw IllegalStateException("foo")
		}

		val a = checkThrows<AssertionError> {
			willThrow()
		}

		val b = checkThrows<AssertionError> {
			willThrow()
		}

		check(a === b)
	}
}
