package opensavvy.prepared.runner.kotest

import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.scopes.ContainerScope
import io.kotest.core.spec.style.scopes.RootScope
import io.kotest.core.spec.style.scopes.addTest
import io.kotest.core.test.TestType
import opensavvy.prepared.suite.SuiteDsl
import opensavvy.prepared.suite.TestDsl
import opensavvy.prepared.suite.config.*
import kotlin.coroutines.CoroutineContext

/**
 * Executes a Prepared [SuiteDsl] in a Kotest suite.
 *
 * Since Kotest cannot represent nested non-suspending test suites, all nested suites are declared at the top-level
 * of the provided [RootScope]. The name of the suite is appended at the start of the tests, so the suite structure is not lost.
 *
 * ### Example
 *
 * This example uses [StringSpec], but tests can be registered using any spec.
 *
 * ```kotlin
 * class MyTests : StringSpec({
 *     "A regular Kotest test" {
 *         // …
 *     }
 *
 *     preparedSuite {
 *         test("A regular Prepared test") {
 *             // …
 *         }
 *
 *         suite("A regular Prepared test suite") {
 *             // …
 *         }
 *     }
 * })
 * ```
 */
@KotestTestScope
fun RootScope.preparedSuite(
	config: TestConfig = TestConfig.Empty,
	block: SuiteDsl.() -> Unit,
) {
	NonNestedSuite(this, config).block()
}

private class NonNestedSuite(private val root: RootScope, private val parentConfig: TestConfig, private val prefix: String? = null) : SuiteDsl {
	override fun suite(name: String, config: TestConfig, block: SuiteDsl.() -> Unit) {
		NonNestedSuite(root, parentConfig + config, prefix child name).block()
	}

	override fun test(name: String, context: CoroutineContext, config: TestConfig, block: suspend TestDsl.() -> Unit) {
		val thisConfig = parentConfig + config

		val kotestConfig = io.kotest.core.test.config.TestConfig(
			enabled = thisConfig[Ignored] == null,
			tags = config[Tag]
				.mapTo(HashSet()) { io.kotest.core.Tag(it.name) }
				.takeIf { it.isNotEmpty() },
			coroutineTestScope = true,
			coroutineDebugProbes = true,
		)

		root.addTest(testName = TestName(name = prefix child name), disabled = false, type = TestType.Test, config = kotestConfig) {
			executeTest(name, context, config, block)
		}
	}
}

// Workaround to avoid using the coroutine dispatcher on KJS.
// See https://gitlab.com/opensavvy/groundwork/prepared/-/issues/59
internal expect suspend fun ContainerScope.executeTest(name: String, context: CoroutineContext, config: TestConfig, block: suspend TestDsl.() -> Unit)

/**
 * Appends [name] at the end of `this`, handling the case where `this` is `null`.
 */
private infix fun String?.child(name: String) =
	when {
		// See https://kotest.io/docs/framework/conditional/conditional-tests-with-focus-and-bang.html
		this != null && name.startsWith("f:") -> "f:$this • $name"
		this != null && name.startsWith("!") -> "!$this • $name"
		this != null -> "$this • $name"
		else -> name
	}
