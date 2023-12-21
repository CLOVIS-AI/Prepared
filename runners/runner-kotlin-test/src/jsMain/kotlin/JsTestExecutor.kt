package opensavvy.prepared.runner.kotlin

import opensavvy.prepared.suite.SuiteDsl
import opensavvy.prepared.suite.TestDsl
import opensavvy.prepared.suite.config.Ignored
import opensavvy.prepared.suite.config.TestConfig
import opensavvy.prepared.suite.config.get
import opensavvy.prepared.suite.config.plus
import opensavvy.prepared.suite.runTestDsl
import kotlin.coroutines.CoroutineContext

// access the internals of kotlin-test, let's hope they don't change in the future :)
// see https://github.com/JetBrains/kotlin/blob/master/libraries/kotlin.test/js/src/main/kotlin/kotlin/test/TestApi.kt
@JsModule("kotlin-test")
@JsNonModule
private external val kTest: dynamic

actual abstract class TestExecutor {

	actual open val config: TestConfig
		get() = TestConfig.Empty

	actual abstract fun SuiteDsl.register()

	// this test shows up as an empty test that always succeeds in reports,
	// but we need it for the class to be discovered
	@kotlin.test.Test
	fun registerTests() {
		val name = "Class ${this::class.simpleName}"
		kTest.kotlin.test.suite(name, false) {
			JsSuiteDsl(name, config).register()
		}
	}
}

private class JsSuiteDsl(val suiteName: String, val parentConfig: TestConfig) : SuiteDsl {
	override fun suite(name: String, config: TestConfig, block: SuiteDsl.() -> Unit) {
		println("Registering suite '$name'…")
		val thisConfig = parentConfig + config
		val thisName = "$suiteName • $name"
		kTest.kotlin.test.suite(thisName, thisConfig[Ignored] != null) {
			JsSuiteDsl(thisName, thisConfig).block()
		}
	}

	override fun test(name: String, context: CoroutineContext, config: TestConfig, block: suspend TestDsl.() -> Unit) {
		println("Registering test '$name'…")
		val thisConfig = parentConfig + config
		kTest.kotlin.test.test(name, thisConfig[Ignored] != null) {
			runTestDsl(name, context, thisConfig, block)
		}
	}
}
