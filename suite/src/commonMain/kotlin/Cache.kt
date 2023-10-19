package opensavvy.prepared.suite

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class Cache {
	private val lock = Mutex()
	private val cache = HashMap<Any, Any?>()

	suspend fun cache(key: Any, compute: suspend () -> Any?) =
		lock.withLock(key) { cache[key] } ?: run {
			val result = compute()

			lock.withLock(key) { cache[key] = result }

			result
		}
}
