package opensavvy.prepared.suite

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class Finalizers {
	private val lock = Mutex()
	private val finalizers = ArrayList<Finalizer>()

	suspend fun register(name: String, block: suspend TestDsl.() -> Unit) {
		lock.withLock("register") { finalizers.add(Finalizer(name, block)) }
	}

	suspend fun TestDsl.executeAllFinalizers() {
		finalizers.asReversed().forEach {
			println("» Running finalizer '${it.name}'")
			it.block(this)
		}
	}

	private class Finalizer(
		val name: String,
		val block: suspend TestDsl.() -> Unit,
	)
}

/**
 * Registers a [block] named [name] to run at the end of the test.
 *
 * The block will run even if the test fails.
 *
 * Finalizers are ran in inverse order as their registration order.
 */
@PreparedDslMarker
suspend fun TestDsl.cleanUp(name: String, block: suspend TestDsl.() -> Unit) {
	environment.finalizers.register(name, block)
}
