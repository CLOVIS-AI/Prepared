package opensavvy.prepared.suite

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import opensavvy.prepared.runner.kotest.PreparedSpec
import opensavvy.prepared.suite.random.nextBoolean
import opensavvy.prepared.suite.random.nextDouble
import opensavvy.prepared.suite.random.nextInt
import opensavvy.prepared.suite.random.random
import opensavvy.prepared.suite.random.randomBoolean
import opensavvy.prepared.suite.random.randomDouble
import opensavvy.prepared.suite.random.randomInt

class RandomTest : PreparedSpec({

	test("Generate random values without setting a seed") {
		check(random.nextInt() != 972016666)
		check(random.nextInt() != 1740578880)
		check(random.nextDouble() != 0.9049568172356872)
	}

	test("Setting the seed guarantees that the same sequence of numbers is generated") {
		random.setSeed(42)

		check(random.nextInt() == 972016666)
		check(random.nextInt() == 1740578880)
		check(random.nextDouble() == 0.9049568172356872)
		check(random.nextBoolean() == false)
	}

	val int1 by randomInt()
	val int2 by randomInt()
	val double by randomDouble()
	val boolean by randomBoolean()

	test("Setting the seed before accessing prepared values guarantees the sequence of numbers") {
		random.setSeed(43)

		check(int1() == -1828752340)
		check(int2() == -1728936224)
		check(double() == 0.9383535655576841)
		check(boolean() == false)
	}

})
