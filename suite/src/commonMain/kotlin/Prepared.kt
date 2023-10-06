package opensavvy.prepared.suite

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KProperty

/**
 * Represents a value that is lazily generated during test execution.
 *
 * ### Usage
 *
 * This helper allows to declare values that are needed by multiple tests, whilst ensuring each test gets its own
 * distinct instance. Within a given test, however, the value is always the same:
 * ```kotlin
 * suite("Random integers") {
 *     val randomInteger by prepared { Random.nextInt() }
 *
 *     test("First test") {
 *         println(randomInteger()) // some integer
 *         println(randomInteger()) // the same integer
 *     }
 *
 *     test("Second test") {
 *         println(randomInteger()) // another integer
 *     }
 * }
 * ```
 *
 * Prepared values are constructed lazily when they are accessed within a test.
 * Because of this, they have access to the test's [TestDsl] and can `suspend`.
 *
 * ### Comparison with other frameworks
 *
 * Test frameworks usually provide a construct like `@BeforeTest` or similar.
 * These constructs allow to declare instantiation code that is run before tests, however:
 * - they are implicitly used by tests: in a large test file, it is difficult to know which ones may impact the test (whereas, prepared values must always be referred to in the test),
 * - they have implicit ordering relationships: some `@BeforeTest` may use the result from previous ones, which makes it hard to know if removing one will impact test results (whereas, prepared values explicitly depend on each other),
 * - they require the need of some kind of `lateinit` variable to store their state (whereas, prepared values' state is available in all test scopes),
 * - they are not compatible with coroutines.
 *
 * Test frameworks also tend to provide a construct like `@AfterTest`, but again, this is covered by [cleanUp].
 * Prepared values can use [cleanUp] as well:
 * ```kotlin
 * val database by prepared(Dispatchers.IO) {
 *     val db = Database.connect()
 *
 *     cleanUp("Disconnect from the database") {
 *         db.close()
 *     }
 *
 *     db
 * }
 *
 * test("Test") {
 *     // if a prepared value is accessed in a test, it is automatically cleaned at the end of the test
 *     database().listTables()
 * }
 * ```
 *
 * Values are instantiated using the [prepared] helper.
 */
class Prepared<out T : Any> internal constructor(
	private val name: String,
	private val block: suspend TestDsl.() -> T,
) {

	@Suppress("UNCHECKED_CAST")
	internal suspend fun executeIn(scope: TestDsl): T =
		scope.environment.cache.cache(this) {
			withContext(CoroutineName("Preparing $name")) {
				val result = scope.block()
				println("» Prepared ‘$name’: $result")
				result
			}
		} as T

	// impl note:
	//    this class *must not* have an equals method
	//    it must always be compared by reference, or the cache risks confusing multiple values
}

/**
 * See [prepared].
 */
class PreparedDelegate<T : Any>(
	private val value: Prepared<T>,
) {
	operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
}

/**
 * See [prepared].
 */
class PreparedProvider<T : Any>(
	private val block: suspend TestDsl.() -> T,
) {
	operator fun provideDelegate(thisRef: Any?, property: KProperty<*>) = PreparedDelegate(
		Prepared(
			name = property.name,
			block = block,
		)
	)
}

/**
 * Declares a lazily-prepared value which will be constructed by calling [block] during test execution.
 *
 * The prepared value returned by this function is automatically named after the variable it is stored in.
 *
 * For more information, see [Prepared].
 *
 * ### Example
 *
 * ```kotlin
 * val randomInteger by prepared { Random.nextInt() }
 * ```
 */
@PreparedDslMarker
fun <T : Any> prepared(
	context: CoroutineContext = EmptyCoroutineContext,
	block: suspend TestDsl.() -> T,
) = PreparedProvider {
	withContext(context) {
		block()
	}
}

/**
 * Declares a lazily-prepared value called [name] which will be constructed by calling [block] during test execution.
 *
 * For more information, see [Prepared].
 *
 * ### Example
 *
 * ```kotlin
 * val randomInteger = prepared("A randomized integer") { Random.nextInt() }
 * ```
 */
@PreparedDslMarker
fun <T : Any> prepared(
	name: String,
	context: CoroutineContext = EmptyCoroutineContext,
	block: suspend TestDsl.() -> T,
) = Prepared(name) {
	withContext(context) {
		block()
	}
}
