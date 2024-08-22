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
	}

	object Full : Display {
		override fun display(value: Any?): String =
			value.toString()
	}
}
