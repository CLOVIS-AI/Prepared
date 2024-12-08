package opensavvy.prepared.suite

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.withContext
import opensavvy.prepared.suite.display.Display
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KProperty

/**
 * Lazily-generated value unique to a test case.
 *
 * ### Usage
 *
 * This helper allows to declare values that are needed by multiple tests, whilst ensuring each test gets its own
 * distinct instance. Within a given test, however, the value is always the same:
 * ```kotlin
 * suite("Random integers") {
 *     val randomInteger by prepared { random.nextInt() }
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
 * For the specific use-case of generating random values, see [random][opensavvy.prepared.suite.random.random].
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
class Prepared<out T> internal constructor(
	val name: String,
	internal val display: Display,
	private val block: suspend TestDsl.() -> T,
) {

	@Suppress("UNCHECKED_CAST")
	internal suspend fun executeIn(scope: TestDsl): T =
		scope.environment.cache.cache(this) {
			withContext(CoroutineName("Preparing $name")) {
				val result = scope.block()
				println("» Prepared ‘$name’: ${display.display(result)}")
				result
			}
		} as T

	// impl note:
	//    this class *must not* have an equals method
	//    it must always be compared by reference, or the cache risks confusing multiple values

	override fun toString() = "\uD83D\uDDA9 $name"
}

/**
 * See [prepared].
 */
class PreparedDelegate<T> internal constructor(
	private val value: Prepared<T>,
) {
	operator fun getValue(thisRef: Any?, property: KProperty<*>) = value
}

/**
 * A [Prepared] is a lazily-created value that is bound to a test, such that multiple reads provide the same value.
 *
 * A [PreparedProvider] represents the operation that would be executed to generate a prepared value, but isn't
 * bound yet. This means [PreparedProvider] is a sort of factory for [Prepared] instances: the same provider can
 * build multiple prepared instances, which run the same operation when executed, but are cached independently.
 *
 * ### Bind a value
 *
 * The simplest way to bind a provider to a single value is through delegation:
 * ```kotlin
 * val prepareRandomInt = prepared { random.nextInt() }
 * val first by prepareRandomInt
 * val second by prepareRandomInt
 *
 * test("A test") {
 *     // Inside this test, 'first' is a specific int, and 'second' is another specific int
 * }
 * ```
 *
 * Instead of binding a provider to a variable, it is also possible to explicitly bind it with [named].
 *
 * ### Use without binding
 *
 * It is also possible to use a provider to generate values without binding them to a [Prepared] instance; see [TestDsl.immediate].
 */
class PreparedProvider<T> internal constructor(
	internal val display: Display,
	internal val block: suspend TestDsl.() -> T,
) {
	/**
	 * Provides a [Prepared] instance bound to the given [name].
	 */
	@Suppress("MemberVisibilityCanBePrivate")
	fun named(name: String) =
		Prepared(name = name, block = block, display = display)

	/**
	 * Provides a [Prepared] instance bound to the given [property].
	 *
	 * ### Example
	 *
	 * ```kotlin
	 * val randomEmail by prepared { "my-account-${random.nextInt()}@mail.com" }
	 * ```
	 */
	operator fun provideDelegate(thisRef: Any?, property: KProperty<*>) =
		PreparedDelegate(named(property.name))
}

@Deprecated("Old variant that didn't allow specifying a custom display. Will be removed in 2.0.0.", level = DeprecationLevel.HIDDEN)
@PreparedDslMarker
fun <T> prepared(
	context: CoroutineContext = EmptyCoroutineContext,
	block: suspend TestDsl.() -> T,
) = prepared(context, Display.Short, block)

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
 * val randomEmail by prepared { "my-account-${random.nextInt()}@mail.com" }
 * ```
 */
@PreparedDslMarker
fun <T> prepared(
	context: CoroutineContext = EmptyCoroutineContext,
	display: Display = Display.Short,
	block: suspend TestDsl.() -> T,
) = PreparedProvider(display) {
	withContext(context) {
		block()
	}
}

@Deprecated("Old variant that didn't allow specifying a custom display. Will be removed in 2.0.0.", level = DeprecationLevel.HIDDEN)
@PreparedDslMarker
fun <T> prepared(
	name: String,
	context: CoroutineContext = EmptyCoroutineContext,
	block: suspend TestDsl.() -> T,
) = prepared(name, context, Display.Short, block)

/**
 * Declares a lazily-prepared value called [name] which will be constructed by calling [block] during test execution.
 *
 * For more information, see [Prepared].
 *
 * ### Example
 *
 * ```kotlin
 * val randomEmail = prepared("A randomized email address") { "my-account-${random.nextInt()}@mail.com" }
 * ```
 */
@PreparedDslMarker
fun <T> prepared(
	name: String,
	context: CoroutineContext = EmptyCoroutineContext,
	display: Display = Display.Short,
	block: suspend TestDsl.() -> T,
) = Prepared(name, display) {
	withContext(context) {
		block()
	}
}
