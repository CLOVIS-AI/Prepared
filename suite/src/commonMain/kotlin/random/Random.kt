/*
 * Copyright (c) 2023-2025, OpenSavvy and contributors.
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

package opensavvy.prepared.suite.random

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import opensavvy.prepared.suite.TestDsl
import opensavvy.prepared.suite.prepared
import kotlin.jvm.JvmName
import kotlin.random.Random.Default.nextBits
import kotlin.random.Random.Default.nextBoolean
import kotlin.random.Random.Default.nextDouble
import kotlin.random.Random.Default.nextFloat
import kotlin.random.Random.Default.nextInt
import kotlin.random.Random.Default.nextLong
import kotlin.random.Random as KotlinRandom

private class ConfiguredRandom(
	private val explicitlyChosen: Boolean,
	private val seed: Long,
) {

	val source = KotlinRandom(seed)

	private val lock = Mutex()

	suspend fun <T> use(block: (KotlinRandom) -> T): T = lock.withLock("use") {
		block(source)
	}

	override fun toString() = "Random generator" + when (explicitlyChosen) {
		true -> " with the explicitly selected seed $seed"
		false -> " with seed $seed. To reproduce this execution, add 'random.setSeed(${seed}L)' at the start of the test, before any random generation."
	}
}

private val seedCacheKey = Any()

// Ensure there is exactly one instance per test
@Suppress("UNCHECKED_CAST")
private val randomSource by prepared {
	val (seed, chosenExplicitly) = environment.cache.cache(seedCacheKey) { nextLong() to false } as Pair<Long, Boolean>
	ConfiguredRandom(chosenExplicitly, seed)
}

/**
 * Random control helper. See [random][random].
 */
class Random internal constructor(private val dsl: TestDsl) {

	/**
	 * Initializes the underlying [Random][KotlinRandom] implementation with [seed].
	 *
	 * Example:
	 * ```kotlin
	 * val int by randomInt()
	 *
	 * test("This is a test") {
	 *     setSeed(123456789L)
	 *
	 *     // Even if prepared values are declared outside the test,
	 *     // as long as they are accessed after the seed is set,
	 *     // they respect the configured seed.
	 *     println("Generated number: ${int()}")
	 * }
	 * ```
	 *
	 * This function is meant to easily allow reproducing a test failure that only arrives in rare cases
	 * by simply adding it at the start of the test with the seed of the failed execution.
	 *
	 * This function can only be called before the first random value is generated for the current test,
	 * otherwise it throws [IllegalStateException].
	 */
	@Suppress("UNCHECKED_CAST")
	suspend fun setSeed(seed: Long) = with(dsl) {
		val (storedSeed, chosenExplicitly) = environment.cache.cache(seedCacheKey) { seed to true } as Pair<Long, Boolean>
		check(storedSeed == seed) { "The random generator has already been configured to use the seed $storedSeed (${if (chosenExplicitly) "explicitly chosen" else "generated randomly on first use"}), impossible to override its seed with $seed" }
	}

	/**
	 * Gives uncontrolled access to the underlying [Random][KotlinRandom].
	 *
	 * **Warning.**
	 * The Kotlin standard library's random generator is not thread-safe.
	 * This class wraps it with the necessary synchronization mechanisms.
	 * By using [accessUnsafe], you are bypassing them.
	 * Incorrect usage of this function may break the random generation and reproducibility guarantees
	 * of this class.
	 *
	 * In most cases, [use] is probably sufficient.
	 */
	suspend fun accessUnsafe(): KotlinRandom = with(dsl) {
		return randomSource().source
	}

	/**
	 * Provides [block] with the underlying [Random][KotlinRandom] source.
	 *
	 * **Warning.**
	 * The Kotlin standard library's random generator is not thread-safe.
	 * This class ensures only a single thread may call this function at once.
	 * It is therefore unsafe to access the provided generator after the call to this function has ended.
	 */
	suspend fun <T> use(block: (KotlinRandom) -> T): T = with(dsl) {
		randomSource().use(block)
	}
}

/**
 * Random generator control center.
 *
 * ## Why?
 *
 * We often need randomness in tests, for example to generate test data, or for property testing.
 * However, randomness hurts reproducibility.
 *
 * When a random value is generated using this helper, the random generator's seed is printed to the test's standard
 * output. If we want to reproduce a previous test execution (e.g. rerun a failed CI test locally),
 * we can add a call to [setSeed][Random.setSeed] at the start of the test.
 *
 * ## Thread-safety
 *
 * The [Random][KotlinRandom] class from the Kotlin standard library is not thread-safe.
 * Since Prepared tests are encouraged to be asynchronous, this would risk making reproducibility impossible.
 *
 * For this reason, Prepared exposes the traditional [nextInt][Random.nextInt]-style functions, which follow the same
 * signature as the [Random][KotlinRandom] class, but hidden behind a lock.
 *
 * ```kotlin
 * test("A test that uses random values") {
 *     val int = random.nextInt()
 *
 *     println("Generated integer: $int")
 * }
 * ```
 *
 * If you really need to access the underlying [Random][KotlinRandom] class, see [Random.accessUnsafe].
 *
 * ## Prepared values
 *
 * When writing tests with Prepared, we often prefer to declare values used in the tests before the test declaration
 * itself, using [prepared] values. However, the functions mentioned above are only available on this helper, which
 * only exists in tests.
 *
 * To simplify this pattern, we also expose [randomInt] and similar functions which expose [prepared providers][opensavvy.prepared.suite.PreparedProvider].
 *
 * ```kotlin
 * val int by randomInt()
 *
 * test("A test that uses random values") {
 *     println("Generated integer: ${int()}")
 * }
 * ```
 *
 * As an added benefit, since prepared values log their actual result, all random values generated this way log themselves.
 */
val TestDsl.random: Random
	get() = Random(this)

// region Direct accessors for kotlin.random.Random

/**
 * Generates random bits.
 * @see KotlinRandom.nextBits Standard library.
 * @see randomBits Prepared value equivalent.
 */
suspend fun Random.nextBits(bitCount: Int) =
	use { it.nextBits(bitCount) }

/**
 * Generates a random integer.
 * @see KotlinRandom.nextInt Standard library.
 * @see randomInt Prepared value equivalent.
 */
suspend fun Random.nextInt() =
	use { it.nextInt() }

/**
 * Generates a random integer.
 * @see KotlinRandom.nextInt Standard library.
 * @see randomInt Prepared value equivalent.
 */
suspend fun Random.nextInt(from: Int, until: Int) =
	use { it.nextInt(from, until) }

/**
 * Generates a random integer.
 * @see KotlinRandom.nextLong Standard library.
 * @see randomLong Prepared value equivalent.
 */
suspend fun Random.nextLong() =
	use { it.nextLong() }

/**
 * Generates a random integer.
 * @see KotlinRandom.nextLong Standard library.
 * @see randomLong Prepared value equivalent.
 */
suspend fun Random.nextLong(from: Long, until: Long) =
	use { it.nextLong(from, until) }

/**
 * Generates a random boolean.
 * @see KotlinRandom.nextBoolean Standard library.
 * @see randomBoolean Prepared value equivalent.
 */
suspend fun Random.nextBoolean() =
	use { it.nextBoolean() }

/**
 * Generates a random double.
 * @see KotlinRandom.nextDouble Standard library.
 * @see randomDouble Prepared value equivalent.
 */
suspend fun Random.nextDouble() =
	use { it.nextDouble() }

/**
 * Generates a random double.
 * @see KotlinRandom.nextDouble Standard library.
 * @see randomDouble Prepared value equivalent.
 */
suspend fun Random.nextDouble(from: Double, until: Double) =
	use { it.nextDouble(from, until) }

/**
 * Generates a random float.
 * @see KotlinRandom.nextFloat Standard library.
 * @see randomFloat Prepared value equivalent.
 */
suspend fun Random.nextFloat() =
	use { it.nextFloat() }

// endregion
// region Prepared values accessors for kotlin.random.Random

/**
 * Provider for random bits.
 * @see random Learn more about random values and reproducibility.
 * @see KotlinRandom.nextBits Standard library.
 * @see nextBits Direct value equivalent.
 */
fun randomBits(bitCount: Int) = prepared { random.nextBits(bitCount) }

/**
 * Provider for a random integer.
 * @see random Learn more about random values and reproducibility.
 * @see KotlinRandom.nextInt Standard library.
 * @see nextInt Direct value equivalent.
 */
fun randomInt() = prepared { random.nextInt() }

/**
 * Provider for a random integer.
 * @see random Learn more about random values and reproducibility.
 * @see KotlinRandom.nextInt Standard library.
 * @see nextInt Direct value equivalent.
 */
fun randomInt(from: Int, until: Int) = prepared { random.nextInt(from, until) }

/**
 * Provider for a random integer.
 * @see random Learn more about random values and reproducibility.
 * @see KotlinRandom.nextLong Standard library.
 * @see nextLong Direct value equivalent.
 */
fun randomLong() = prepared { random.nextLong() }

/**
 * Provider for a random integer.
 * @see random Learn more about random values and reproducibility.
 * @see KotlinRandom.nextLong Standard library.
 * @see nextLong Direct value equivalent.
 */
fun randomLong(from: Long, until: Long) = prepared { random.nextLong(from, until) }

/**
 * Provider for a random boolean.
 * @see random Learn more about random values and reproducibility.
 * @see KotlinRandom.nextBoolean Standard library.
 * @see nextBoolean Direct value equivalent.
 */
fun randomBoolean() = prepared { random.nextBoolean() }

/**
 * Provider for a random double.
 * @see random Learn more about random values and reproducibility.
 * @see KotlinRandom.nextDouble Standard library.
 * @see nextDouble Direct value equivalent.
 */
fun randomDouble() = prepared { random.nextDouble() }

/**
 * Provider for a random double.
 * @see random Learn more about random values and reproducibility.
 * @see KotlinRandom.nextDouble Standard library.
 * @see nextDouble Direct value equivalent.
 */
fun randomDouble(from: Double, until: Double) = prepared { random.nextDouble(from, until) }

/**
 * Provider for a random float.
 * @see random Learn more about random values and reproducibility.
 * @see KotlinRandom.nextFloat Standard library.
 * @see nextFloat Direct value equivalent.
 */
fun randomFloat() = prepared { random.nextFloat() }

// endregion
// region Warn on usages of kotlin.random.Random in tests

private const val DEPRECATION_MESSAGE_WRONG_RANDOM = "Using Random in a Prepared test is most likely a mistake. Either opt-in to using the Prepared reproducible random generators (via the 'random' accessor in a test) or explicitly use kotlin.random.Random via qualified name to suppress this warning."

/**
 * See [random].
 */
@Deprecated(DEPRECATION_MESSAGE_WRONG_RANDOM, ReplaceWith("random"), DeprecationLevel.WARNING)
@get:JvmName("getKotlinRandom")
val TestDsl.Random: Random
	get() = random

// endregion
