package opensavvy.playground.core

import io.kotest.core.spec.style.FunSpec
import opensavvy.prepared.runner.kotest.PreparedSpec

class HelloWorldKotestTest : PreparedSpec({

	test("Hello world!") {
		@Suppress("SimplifyBooleanWithConstants") // The compiler knows this test will always succeed
		check("Hello World!" == message)
	}
})

class FooTest : FunSpec({
	test("test") {
		check(true)
	}
})
