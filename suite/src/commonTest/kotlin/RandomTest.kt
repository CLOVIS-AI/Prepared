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

})
