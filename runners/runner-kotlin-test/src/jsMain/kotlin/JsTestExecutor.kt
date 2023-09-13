package opensavvy.prepared.runner.kotlin

import kotlinx.coroutines.test.runTest
import opensavvy.prepared.suite.SuiteDsl
import opensavvy.prepared.suite.TestDsl
import kotlin.coroutines.CoroutineContext

// access the internals of kotlin-test, let's hope they don't change in the future :)
// see https://github.com/JetBrains/kotlin/blob/master/libraries/kotlin.test/js/src/main/kotlin/kotlin/test/TestApi.kt
@JsModule("kotlin-test")
@JsNonModule
private external val kTest: dynamic

actual abstract class TestExecutor {

	actual abstract fun SuiteDsl.register()

	// this test shows up as an empty test that always succeeds in reports,
	// but we need it for the class to be discovered
	@kotlin.test.Test
	fun registerTests() {
		kTest.kotlin.test.suite("Class ${this::class.simpleName}", false) {
			JsSuiteDsl.register()
		}
	}
}

private object JsSuiteDsl : SuiteDsl {
	override fun suite(name: String, block: SuiteDsl.() -> Unit) {
		println("Registering suite '$name'…")
		kTest.kotlin.test.suite(name, false) {
			this.block()
		}
	}

	override fun test(name: String, context: CoroutineContext, block: suspend TestDsl.() -> Unit) {
		println("Registering test '$name'…")
		kTest.kotlin.test.test(name, false) {
			runTest(context) { block(JsTestDsl) }
		}
	}
}

private object JsTestDsl : TestDsl
