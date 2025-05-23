package opensavvy.prepared.runner.testballoon

import de.infix.testBalloon.framework.testSuite

val TestBalloonSuite by testSuite {
	test("string length") {
		check(8 == "Test me!".length)
	}
	
	testSuite("Foo") {
		test("First") {
			println("Success")
		}
		
		test("Second") {
			println("Success")
		}
		
		withPrepared { 
			test("Third") {
				println("Success")
			}
			
			test("Fourth") {
				println("Success")
			}
		}
	}
}

val PreparedBalloonedSuite by preparedSuite {
	test("Foo") {
		println("Success")
	}

	suite("Bar") {
		test("First") {
			println("Success")
		}

		test("Second") {
			println("Success")
		}
	}
}
