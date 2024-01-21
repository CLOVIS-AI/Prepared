package opensavvy.prepared.suite

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class Finalizers {
	private val lock = Mutex()
	private val finalizers = ArrayList<Finalizer>()

	suspend fun register(
		name: String,
		onSuccess: Boolean,
		onFailure: Boolean,
		block: suspend TestDsl.() -> Unit,
	) {
		lock.withLock("register") { finalizers.add(Finalizer(name, onSuccess, onFailure, block)) }
	}

	suspend fun TestDsl.executeAllFinalizers(successful: Boolean) {
		finalizers.asReversed()
			.filter { successful && it.onSuccess || !successful && it.onFailure }
			.forEach {
				println("» Finalizing '${it.name}'")
				it.block(this)
			}
	}

	private class Finalizer(
		val name: String,
		val onSuccess: Boolean,
		val onFailure: Boolean,
		val block: suspend TestDsl.() -> Unit,
	)
}

/**
 * Registers a [block] named [name] to run at the end of the test.
 *
 * The block will run even if the test fails.
 *
 * Finalizers are ran in inverse order as their registration order.
 *
 * ### Example
 *
 * ```kotlin
 * val prepareDatabase by prepared { FakeDatabase() }
 *
 * test("Create a user") {
 *     val database = prepareDatabase()
 *
 *     val user = database.createUser()
 *
 *     cleanUp("Delete user $user") {
 *         database.deleteUser(user)
 *     }
 * }
 * ```
 *
 * ### Declaration from within prepared values
 *
 * This function can be called from within a [prepared] value, in which case it will run when the test that initialized that prepared value finishes:
 * ```kotlin
 * val prepareDatabase by prepared {
 *     FakeDatabase()
 *         .also { cleanUp("Disconnect from the database") { it.disconnect() } }
 * }
 *
 * val prepareUser by prepared {
 *     val database = prepareDatabase()
 *     database.createUser()
 *         .also { cleanUp("Delete user $it") { database.deleteUser(it) } }
 * }
 *
 * test("Rename a user") {
 *     val user = prepareUser()
 *
 *     user.rename("New name")
 *
 *     // will automatically run:
 *     // 1. Delete user …
 *     // 2. Disconnect from the database
 * }
 * ```
 *
 * @param onSuccess If `false`, this finalizer will not run if the test failed.
 * @param onFailure If `false`, this finalizer will not run if the test was successful.
 */
@PreparedDslMarker
suspend fun TestDsl.cleanUp(
	name: String,
	onSuccess: Boolean = true,
	onFailure: Boolean = true,
	block: suspend TestDsl.() -> Unit,
) {
	environment.finalizers.register(name, onSuccess, onFailure, block)
}
