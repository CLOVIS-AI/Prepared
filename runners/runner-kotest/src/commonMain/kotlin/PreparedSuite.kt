package opensavvy.prepared.runner.kotest

import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.scopes.RootScope
import io.kotest.core.spec.style.scopes.addTest
import io.kotest.core.test.TestType
import opensavvy.prepared.suite.SuiteDsl
import opensavvy.prepared.suite.TestDsl
import opensavvy.prepared.suite.runTestDsl
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
fun RootScope.preparedSuite(block: SuiteDsl.() -> Unit) {
	NonNestedSuite(this).block()
}

private class NonNestedSuite(private val root: RootScope, private val prefix: String? = null) : SuiteDsl {
	override fun suite(name: String, block: SuiteDsl.() -> Unit) {
		NonNestedSuite(root, prefix child name).block()
	}

	override fun test(name: String, context: CoroutineContext, block: suspend TestDsl.() -> Unit) {
		root.addTest(testName = TestName(name = prefix child name), disabled = false, type = TestType.Test, config = null) {
			runTestDsl(name, context, block)
		}
	}
}

/**
 * Appends [name] at the end of `this`, handling the case where `this` is `null`.
 */
private infix fun String?.child(name: String) =
	if (this != null) "$this • $name"
	else name
