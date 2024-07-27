package opensavvy.prepared.runner.kotest

import io.kotest.matchers.shouldBe

class FocusTest : PreparedSpec({

	test("f:This test is focused, and should run") {
		true shouldBe true
	}

	test("This test is not focused, and should not run") {
		error("This test doesn't start with 'f:', meaning it shouldn't run")
	}

	suite("f:This suite is focused, and should run") {
		test("First test") {
			true shouldBe true
		}

		test("Second test") {
			true shouldBe true
		}
	}

	suite("This suite is not focused, and should not run") {
		test("This test is inside a non-focused test, and should therefore not run due to the top-level focused test") {
			error("A top-level is focused, and this test isn't, and its suite isn't, so it shouldn't run")
		}

		test("f:This test is focused") {
			true shouldBe true
		}
	}

})
