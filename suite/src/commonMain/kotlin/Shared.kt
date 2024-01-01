package opensavvy.prepared.suite

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KProperty

/**
 * Pure, immutable lazy value which is shared between all tests.
 *
 * Most of the time, we recommend using [Prepared] instead.
 *
 * ### Usage
 *
 * This helper allows to declare values that are needed by multiple tests.
 * The first test to access this value computes it, after which all other tests access the same value.
 *
 * ```kotlin
 * suite("Random integers") {
 *     val randomInteger by shared { someLongOperation() }
 *
 *     test("First test") {
 *         println(randomInteger()) // some integer
 *         println(randomInteger()) // the same integer
 *     }
 *
 *     test("Second test") {
 *         println(randomInteger()) // still the same integer
 *     }
 * }
 * ```
 * Notice the difference with [Prepared]:
 * - using `Prepared`, all calls within the same test give the same value, but each test gets its own value.
 * - using `Shared`, all calls give the same value, even if they are in different tests.
 *
 * Values are instantiated using the [shared] helper.
 *
 * ### When to use
 *
 * Only use this class to hold values that are deeply immutable, are produced by pure operations, and are too costly
 * to rerun every test.
 *
 * **Deep immutability** is necessary because otherwise a test could modify the value and change the behavior of
 * another test.
 *
 * **Produced by pure operations** because the value is only computed once in the context of the first test which
 * accesses it. Side effects, if any, will only be observable in the first test.
 *
 * Additionally, **shared values cannot access most features of this library**, including [time control][TestDsl.time],
 * [randomness control][TestDsl.random], [finalizers][TestDsl.cleanUp], etc.
 * This is because these features are based on side effects, which this class swallows silently.
 *
 * To summarize:
 * - if the value is mutable, use [Prepared] to ensure tests stay independent.
 * - if the value is produced using a side effect, use [Prepared] to ensure all tests can observe it.
 * - if the value is cheap to produce, use [Prepared] because tests being independent make them easier to debug.
 */
class Shared<out T> internal constructor(
	val name: String,
	private val block: suspend () -> T,
) {

	private val lock = Mutex()
	private var result: Option<T> = Option.Empty

	/**
	 * Computes the shared value, or returns the cached value if it has already been computed.
	 */
	suspend operator fun invoke(): T {
		var fromHere: Boolean
		lock.withLock {
			if (result is Option.Empty) {
				result = Option.Present(block())
				fromHere = true
			} else {
				fromHere = false
			}
		}

		val stored = result
		check(stored is Option.Present) { "The stored result is $stored, even though we just passed the block that is expected to initialize it, that should be impossible" }
		println("» Shared ‘${name}’: ${stored.value} " + if (fromHere) "(initialized by this test)" else "(reusing an already initialized value)")
		return stored.value
	}

	// Implementation detail to avoid null merging,
	// because we need to store a T?, but T may itself be null
	private sealed class Option<out T> {
		data object Empty : Option<Nothing>()
		data class Present<out T>(val value: T) : Option<T>()
	}

	override fun toString() = "Shared($name)"
}

/**
 * See [shared].
 */
class SharedDelegate<T> internal constructor(
	private val value: Shared<T>,
) {
	operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
}

/**
 * A [Shared] is a lazily-created value that is reused between tests.
 *
 * > Most of the time, [PreparedProvider] should be preferred to this class.
 * > To learn why, see [Shared].
 *
 * Although [SharedProvider] is conceptually equivalent to [PreparedProvider], and can be used
 * to generate multiple [Shared] instances from the same block in exactly the same way,
 * this is not recommended.
 *
 * This is because there is no way to parameterize the encapsulated block which is used to generate the values.
 * Therefore, the only way for the block to give different values on each execution is if it relies on side
 * effects. However, as explained in the [Shared] documentation, shared values should not be used when
 * side effects are present. Instead, using [Prepared] and [PreparedProvider], which encapsulate side effects safely.
 *
 * This class exists because of its other use-case: capturing the name of the property it is instantiated to,
 * using the `by` keyword. See [shared] and [provideDelegate].
 */
class SharedProvider<T> internal constructor(
	private val block: suspend () -> T,
) {

	/**
	 * Provides a [Shared] value instance bound to the given [name].
	 *
	 * This is not recommended, because it likely means you are relying on side effects.
	 * See [Shared] and [SharedProvider] for an explanation.
	 */
	@Deprecated("The primary use-case for this method is to generate multiple shared values from a single provider. This implies you are relying on side effects in the shared value generation. This is not recommended. See the documentation of Shared and SharedProvider to learn more.")
	fun named(name: String) =
		Shared(name = name, block = block)

	/**
	 * Provides a [Shared] instance bound to the given [property].
	 *
	 * @see shared
	 */
	operator fun provideDelegate(thisRef: Any?, property: KProperty<*>) =
		SharedDelegate(Shared(property.name, block))
}

/**
 * Declares a lazily-computed value that is constructed by calling [block], and is then shared between all tests.
 *
 * > Most of the time, [prepared] should be preferred to this helper. To learn why, see [Shared].
 *
 * The shared value returned by this function is automatically named after the variable it is stored in.
 *
 * For more information, see [Shared].
 *
 * ### Example
 *
 * ```kotlin
 * val precompute by shared { longRunningOperation() }
 * ```
 */
@PreparedDslMarker
fun <T> shared(
	context: CoroutineContext = EmptyCoroutineContext,
	block: suspend () -> T,
) = SharedProvider {
	withContext(context) {
		block()
	}
}

/**
 * Declares a lazily-computer value called [name] that is constructed by calling [block], and is then shared between all tests.
 *
 * > Most of the time, [prepared] should be preferred to this helper. To learn why, see [Shared].
 *
 * For more information, see [Shared].
 *
 * ### Example
 *
 * ```
 * val precompute by shared("A costly operation") { longRunningOperation() }
 * ```
 */
@PreparedDslMarker
fun <T> shared(
	name: String,
	context: CoroutineContext = EmptyCoroutineContext,
	block: suspend () -> T,
) = Shared(name) {
	withContext(context) {
		block()
	}
}
