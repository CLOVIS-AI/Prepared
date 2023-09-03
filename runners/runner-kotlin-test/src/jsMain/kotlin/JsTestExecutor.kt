package opensavvy.prepared.runner.kotlin

import kotlinx.coroutines.test.runTest
import opensavvy.prepared.suite.Suite
import opensavvy.prepared.suite.Test
import kotlin.coroutines.CoroutineContext

// access the internals of kotlin-test, let's hope they don't change in the future :)
// see https://github.com/JetBrains/kotlin/blob/master/libraries/kotlin.test/js/src/main/kotlin/kotlin/test/TestApi.kt
@JsModule("kotlin-test")
@JsNonModule
private external val kTest: dynamic

actual abstract class TestExecutor {

	actual abstract fun Suite.register()

	// this test shows up as an empty test that always succeeds in reports,
	// but we need it for the class to be discovered
	@kotlin.test.Test
	fun registerTests() {
		kTest.kotlin.test.suite("Class ${this::class.simpleName}", false) {
			JsSuite.register()
		}
	}
}

private object JsSuite : Suite {
	override fun suite(name: String, block: Suite.() -> Unit) {
		println("Registering suite '$name'…")
		kTest.kotlin.test.suite(name, false) {
			this.block()
		}
	}

	override fun test(name: String, context: CoroutineContext, block: suspend Test.() -> Unit) {
		println("Registering test '$name'…")
		kTest.kotlin.test.test(name, false) {
			runTest(context) { block(JsTest) }
		}
	}
}

private object JsTest : Test
