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

package opensavvy.prepared.compat.arrow.coroutines

import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.ExitCase.Companion.ExitCase
import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.ResourceDSL
import arrow.fx.coroutines.ResourceScope
import kotlinx.coroutines.Dispatchers
import opensavvy.prepared.suite.*

/**
 * A scope to combine Arrow's [ResourceScope] with Prepared's [TestDsl].
 *
 * This scope is used within tests that declare resources.
 *
 * Resources declared in this scope are released at the end of the test.
 *
 * Instances of this type can be obtained:
 * - As a test fixture with [preparedResource]
 * - Directly, with [resourceScope] (useful for passing the test lifecycle to a component)
 *
 * Additionally:
 * - The [install] functions allows binding a resource directly within a test.
 * - The [asPrepared] function converts a [Resource] into a [Prepared] value, Prepared's equivalent concept.
 */
@ResourceDSL
interface TestResourceScope : ResourceScope, TestDsl

/**
 * Declares a [prepared value][Prepared] that contains Arrow Resources.
 *
 * The resources are released at the end of the test.
 *
 * ### Example
 *
 * ```kotlin
 * val userProcessor: Resource<UserProcessor> = resource({
 *     UserProcessor().also { it.start() }
 * }) { p, _ -> p.shutdown() }
 *
 * val dataSource: Resource<DataSource> = resource({
 *     DataSource().also { it.connect() }
 * }) { ds, exitCase ->
 *     println("Releasing $ds with exit: $exitCase")
 *     withContext(Dispatchers.IO) { ds.close() }
 * }
 *
 * // Note the 'by'!
 * val service by preparedResource {
 *     Service(dataSource.bind(), userProcessor.bind())
 * }
 *
 * test("Some kind of test") {
 *     service().createEntity("…")
 * }
 * ```
 *
 * ### External resources
 *
 * - [Arrow Resource documentation](https://arrow-kt.io/learn/coroutines/resource-safety/)
 *
 * @see TestResourceScope Overview of Prepared and Arrow's compatibility.
 * @see asPrepared Convert a [Resource] into a [Prepared] value.
 * @see install Install a [Resource] directly within a test.
 */
fun <T> preparedResource(block: suspend TestResourceScope.() -> T): PreparedProvider<T> = prepared {
	resourceScope().block()
}

/**
 * Converts an Arrow [Resource] into a [Prepared] value.
 *
 * Prepared's prepared values are similar in concept to resources:
 * - They both are lazy (declaring them and executing them happens in two different steps)
 * - They both contain cleanup logic
 *
 * The resulting prepared value executes the clean-up logic at the end of the test.
 *
 * ### Example
 *
 * ```kotlin
 * val userProcessor: Resource<UserProcessor> = resource({
 *   UserProcessor().also { it.start() }
 * }) { p, _ -> p.shutdown() }
 *
 * val dataSource: Resource<DataSource> = resource({
 *   DataSource().also { it.connect() }
 * }) { ds, exitCase ->
 *   println("Releasing $ds with exit: $exitCase")
 *   withContext(Dispatchers.IO) { ds.close() }
 * }
 *
 * val service: Resource<Service> = resource {
 *   Service(dataSource.bind(), userProcessor.bind())
 * }
 *
 * // Note the 'by'!
 * val prepareService by service.asPrepared()
 *
 * test("Test the creation") {
 *     check(prepareService().create("…") != null)
 * }
 * ```
 *
 * At the end of the test, `service`, `userProcessor` and `dataSource` are cleaned-up (in this order).
 *
 * ### External resources
 *
 * - [Arrow Resource documentation](https://arrow-kt.io/learn/coroutines/resource-safety/)
 *
 * @see TestResourceScope Overview of Prepared and Arrow's compatibility.
 * @see preparedResource Access a [ResourceScope] to declare multiple resources as a single [Prepared] value.
 * @see install Install a [Resource] directly within a test.
 */
fun <T> Resource<T>.asPrepared() = preparedResource {
	this@asPrepared.bind()
}

/**
 * Allows accessing the test lifecycle as an Arrow's [ResourceScope].
 *
 * Resources installed in the returned scope are released at the end of the test.
 *
 * ### External resources
 *
 * - [Arrow Resource documentation](https://arrow-kt.io/learn/coroutines/resource-safety/)
 *
 * @see TestResourceScope Overview of Prepared and Arrow's compatibility.
 * @see preparedResource Install multiple resources into a [Prepared] value.
 * @see asPrepared Convert a [Resource] into a [Prepared] value.
 * @see install Install a [Resource] directly within a test.
 */
fun TestDsl.resourceScope(): TestResourceScope =
	TestResourceScopeImpl(this)

/**
 * Installs an [acquire] action and its matching [release] action.
 *
 * This is a convenience equivalent of [ResourceScope.install] directly on the test.
 * The behavior is identical to calling [ResourceScope.install] on the test's [resourceScope].
 *
 * ### External resources
 *
 * - [Arrow Resource documentation](https://arrow-kt.io/learn/coroutines/resource-safety/)
 *
 * @param acquire The acquire action, executed immediately.
 * @param release The release action, executed at the end of the test.
 * @see TestResourceScope Overview of Prepared and Arrow's compatibility.
 * @see preparedResource Install multiple resources into a [Prepared] value.
 * @see asPrepared Convert a [Resource] into a [Prepared] value.
 */
suspend fun <A> TestDsl.install(
	acquire: suspend TestDsl.() -> A,
	release: suspend TestDsl.(A, ExitCase) -> Unit,
): A =
	resourceScope().install(
		acquire = { acquire() },
		release = { a, case -> release(a, case) },
	)

/**
 * Installs a [Resource] into the test's lifecycle scope.
 *
 * The resource is immediately installed.
 * It is released at the end of the test.
 *
 * This method has identical behavior to [ResourceScope.bind] on the test's [resourceScope].
 * It cannot be called `.bind()` because this would require two receivers, and context parameters are not yet stable.
 *
 * ### External resources
 *
 * - [Arrow Resource documentation](https://arrow-kt.io/learn/coroutines/resource-safety/)
 *
 * @see TestResourceScope Overview of Prepared and Arrow's compatibility.
 * @see preparedResource Install multiple resources into a [Prepared] value.
 * @see asPrepared Convert a [Resource] into a [Prepared] value.
 */
suspend fun <A> TestDsl.install(
	resource: Resource<A>,
): A = with(resourceScope()) {
	resource.bind()
}

private class TestResourceScopeImpl(
	private val test: TestDsl,
) : TestResourceScope, ResourceScope, TestDsl by test {
	override fun onRelease(release: suspend (ExitCase) -> Unit): Unit = with(test) {
		launch(Dispatchers.Unconfined) {
			cleanUp("Release resource on success", onFailure = false) {
				release(ExitCase.Completed)
			}

			cleanUp("Release resource on failure", onSuccess = false) {
				release(ExitCase(RuntimeException("The real exception which caused the test to fail is unknown")))
			}
		}
	}
}
