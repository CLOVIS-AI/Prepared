package opensavvy.prepared.runner.kotlin

import opensavvy.prepared.suite.SuiteDsl
import opensavvy.prepared.suite.TestDsl
import opensavvy.prepared.suite.config.Ignored
import opensavvy.prepared.suite.config.TestConfig
import opensavvy.prepared.suite.config.get
import opensavvy.prepared.suite.config.plus
import opensavvy.prepared.suite.runTestDsl
import kotlin.test.FrameworkAdapter

// region Register a kotlin-test FrameworkAdapter
// See https://youtrack.jetbrains.com/issue/KT-65360#focus=Comments-27-9069942.0-0
// See https://youtrack.jetbrains.com/issue/KT-65360#focus=Comments-27-9112906.0-0

private var currentAdapter: FrameworkAdapter? = null

@OptIn(ExperimentalStdlibApi::class)
@EagerInitialization // Necessary to initialize top-level property in a non-lazy manner
@Suppress("unused") // Automatically called when the program starts because it's eager
private val initializeAdapter = run {
	val jso = js("{}")
	val currentTransformer: ((FrameworkAdapter) -> FrameworkAdapter)? = globalThis.kotlinTest?.adapterTransformer
	jso.adapterTransformer = { previousAdapter: FrameworkAdapter ->
		currentAdapter = previousAdapter
		currentTransformer?.let { it(previousAdapter) }
	}
	globalThis.kotlinTest = jso
}

private external val globalThis: dynamic

// endregion

actual abstract class TestExecutor {

	actual open val config: TestConfig
		get() = TestConfig.Empty

	actual abstract fun SuiteDsl.register()

	@kotlin.test.Test
	fun registerTests() {
		val frameworkAdapter = currentAdapter
			?: error("No framework adapter were defined, or Prepared couldn't register itself into the existing framework adapter.")

		val name = "Class ${this::class.simpleName}"
		frameworkAdapter.suite(name, ignored = false) {
			JsSuiteDsl(frameworkAdapter, name, config).register()
		}
	}
}

private class JsSuiteDsl(val adapter: FrameworkAdapter, val suiteName: String, val parentConfig: TestConfig) : SuiteDsl {
	override fun suite(name: String, config: TestConfig, block: SuiteDsl.() -> Unit) {
		println("Registering suite '$name'…")
		val thisConfig = parentConfig + config
		val thisName = "$suiteName • $name"
		adapter.suite(thisName, thisConfig[Ignored] != null) {
			JsSuiteDsl(adapter, thisName, thisConfig).block()
		}
	}

	override fun test(name: String, config: TestConfig, block: suspend TestDsl.() -> Unit) {
		println("Registering test '$name'…")
		val thisConfig = parentConfig + config
		adapter.test(name, thisConfig[Ignored] != null) {
			runTestDsl(name, thisConfig, block)
		}
	}
}
