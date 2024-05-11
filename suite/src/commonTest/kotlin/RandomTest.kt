package opensavvy.prepared.suite

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import opensavvy.prepared.runner.kotest.PreparedSpec

class RandomTest : PreparedSpec({

	test("Setting the seed guarantees that the same sequence of numbers is generated") {
		random.setSeed(42)

		assertSoftly {
			random.nextInt() shouldBe 972016666
			random.nextInt() shouldBe 1740578880
			random.nextDouble() shouldBe 0.9049568172356872
			random.nextBoolean() shouldBe false
		}
	}

	val int1 by randomInt()
	val int2 by randomInt()
	val double by randomDouble()
	val boolean by randomBoolean()

	test("Setting the seed before accessing prepared values guarantees the sequence of numbers") {
		random.setSeed(43)

		assertSoftly {
			int1() shouldBe -1828752340
			int2() shouldBe -1728936224
			double() shouldBe 0.9383535655576841
			boolean() shouldBe false
		}
	}

})
