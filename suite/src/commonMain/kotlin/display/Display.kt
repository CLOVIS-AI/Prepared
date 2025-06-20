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

package opensavvy.prepared.suite.display

import opensavvy.prepared.suite.Prepared
import opensavvy.prepared.suite.Shared

/**
 * An object responsible for displaying data to the test output.
 *
 * This object is used to allow customization of test display when using [Prepared] and [Shared].
 */
fun interface Display {

	/**
	 * Converts a value to a [String].
	 */
	fun display(value: Any?): String

	object Short : Display {
		override fun display(value: Any?): String {
			val text = value.toString()
				.replace("\n", " / ")

			return if (text.length > 256)
				text.substring(0, 256) + "… (pass Display.Full to view the full value)"
			else
				text
		}

		override fun toString() = "Display.Short"
	}

	object Full : Display {
		override fun display(value: Any?): String =
			value.toString()

		override fun toString() = "Display.Full"
	}
}
