package opensavvy.prepared.compat.parameterize

import com.benwoodworth.parameterize.ExperimentalParameterizeApi
import com.benwoodworth.parameterize.parameterOf
import com.benwoodworth.parameterize.parameterize
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import opensavvy.prepared.compat.kotlinx.datetime.now
import opensavvy.prepared.compat.kotlinx.datetime.set
import opensavvy.prepared.runner.kotest.preparedSuite
import opensavvy.prepared.suite.*
import opensavvy.prepared.suite.random.nextInt
import opensavvy.prepared.suite.random.random
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@OptIn(ExperimentalParameterizeApi::class)
class ParamaterizeTests : StringSpec({
	preparedSuite {
		parameterize {
			val int by parameterOf(1, 2)

			test("Eager test for value $int") {
				(int > 0) shouldBe true
			}
		}

		parameterize {
			val int by parameterOf(1, 2)
				.prepare { it.toString() }

			test("Lazy test for value $int") {
				(int() in "123456789") shouldBe true
			}
		}

		parameterize {
			val currentTime by parameterOf("2022-12-31T23:37:00Z", "2000-01-01T23:37:00Z")
				.prepare { time.set(it) }

			val seed by parameterOf(0L, 13, -1)
				.prepare { random.setSeed(it) }

			val configure by prepared {
				currentTime()
				seed()
			}

			test("Test at $currentTime") {
				configure()

				time.advanceBy(5.minutes)
				println(time.now)

				println(random.nextInt())
			}
		}
	}
})
