package opensavvy.prepared.runner.kotest

import io.kotest.matchers.shouldBe

class BangTest : PreparedSpec({

	test("!This test is skipped because of the bang") {
		error("This test starts with a bang, so it should not execute")
	}

	test("This test should run") {
		true shouldBe true
	}

	suite("!This suite is skipped") {
		test("This test shouldn't run") {
			error("This test should not execute due to the bang in the suite name")
		}
	}

	suite("This suite is not skipped") {
		test("This test is not skipped") {
			true shouldBe true
		}

		test("!This test is skipped") {
			error("This test should not execute due to the bang")
		}
	}

})
