# Features overview

Prepared adds many helpers to facilitate writing tests. To learn about them, either select them in the menu on the left of this page, or click on one of the annotations in this complete example:

??? warning "This example is not idiomatic!"
    This example is written specifically to showcase all features in a single place. It is not considered idiomatic.
    For more information, visit [the best practices section](../practices/index).

??? note "Assertion library"
    This example uses the Kotest assertion library.
    [Learn more about assertion libraries](../tutorials/index.md#assertion-libraries).

## Basic

Tests are declared in suites:

```kotlin
fun SuiteDsl.testUsers() = suite("Test users") {
	// A test is declared by calling the 'test' function
	test("A simple test") {
		UserService().listUsers() shouldBe emptyList()
	}
    
	// Suites can be nested however you want
	suite("A nested suite") {
		suite("Another nested suite") {
			test("A test") {
				// …
			}
            
			test("Another test") {
				// …
			}
		}
        
		repeat(10) { //(1)!
			test("Test #$it") {
				// …
			}
		}
	}
}
```

1.  Because tests are declared as regular function calls, any Kotlin language feature, like loops, can be used to declare complex suites. No need to learn annotation-based test parameterization anymore!

To learn more about tests and suites, see [the dedicated page](../tutorials/syntax.md).

## Reusing data between tests

Test data and initialization logic can be reused between tests by declaring them in [prepared values](prepared-values.md):

```kotlin
val database by prepared { //(3)!
	delay(200)
	Database.connect()
		.also { cleanUp("Disconnect from the test database") { it.close() } } //(6)!    
}

fun SuiteDsl.testUsers(
	users: Prepared<UserService>, //(1)!
) = suite("Users") {
	test("Create a user") {
		users().create("user@mail.com") //(2)!
			shouldBe User(email = "user@mail.com")
	}
    
	val userId by randomInt(0, 9999) //(4)!
	
    val email by prepared {
		"user-${userId()}@mail.com" //(5)!
	}
	
    val testUser by prepared {
		users().create(email())
 	}
    
	test("List users") {
		testUser() //(7)!
		users().findById(userId()).orThrow() //(8)!
	}
    
	test("Hello world") {
		//(9)! 
	}
}
```

1.  Declaring a suite that accepts a [prepared value](prepared-values.md) as parameter is a very common way to reuse tests. 
    For example, if `UserService` is an interface, this function can be called once for each implementation, ensuring all implementations pass the same tests.
2.  A [prepared value](prepared-values.md), here `users` is accessed by calling it, like a function. The value can only be accessed within a test or another prepared value. Each test gets a new value.
3.  [Prepared values](prepared-values.md) can be declared anywhere: in tests, in suites, in classes, even at the file top level. Their value is generated lazily in the context of the test that uses it, meaning they can `suspend`, use all functionality available in test bodies.
4.  This library delivers many utilities in the form of [prepared values](prepared-values.md). In this example, we use the utilities to [generate random values](random.md).
5.  The [prepared value](prepared-values.md) initialization block behaves exactly as a test's block. Therefore, we can refer to other prepared values and use any other feature from this library.
6.  [Finalizers](finalizers.md) declare some code that will be executed at the end of the test.
7.  Since [prepared values](prepared-values.md) can depend on each other, this line will initialize `testUser`, `users`, `email` and `userId`. Each initialized prepared value is printed, along with its value, to make it easy to understand where data comes from.
8.  Because [prepared values](prepared-values.md) only generate a single value per test, we can refer to the same values multiple times without caring whether they have already been initialized or not. This makes test writing much easier.
9.  If a test doesn't refer to any [prepared values](prepared-values.md), none will be initialized. This ensures that declaring a new prepared value, even at the top-level, will never impact any test in which it isn't explicitly referred to.

## Controlling the external world

Good tests are entirely deterministic—but the systems we want to test rarely are. Prepared offers multiple ways to control data from the external world that risks polluting our test results:

```kotlin
fun SuiteDsl.testUsers(
	users: Prepared<UserService>,
) {
	
	suite("Use random values") {
		test("A simple test that uses a random value…") {
			println(random.nextInt()) //(1)!
		}
		
		test("…but we can make it deterministic") {
			random.setSeed(42) //(2)!
			println(random.nextInt() == 972016666) //(3)!
		}
        
		val first by randomInt() //(4)!
		val second by randomInt()
      
		test("This is astronomically unlikely to fail") {
			check(first() != second()) //(5)!
		}
	}
    
	suite("Control the time") {
		test("Test what happens on New Year") {
			time.set("2024-12-31T23:59:59Z") //(6)!
            
			val service = NewYearCardService(time.clock) //(7)!
            
			var congratulated = false
			service.onNewYear { congratulated = true }
			
			time.delayUntil("2025-01-01T02:24:32Z") //(8)!
			check(congratulated)
		}
	}
    
	suite("Control the filesystem") {
		val workingDirectory by createRandomDirectory() //(9)!
		val readme by workingDirectory / "README.md" //(10)!
        
		suite("The README contains the license") {
			createNewProject(workingDirectory())
			
			readme().readText() shouldContain "Licensed under Apache 2.0"
		}
	}
}
```

1.  Prints a random integer. The random seed will be printed to the output so you are able to reproduce the test reliably if it fails. [Learn more](random.md).
2.  The seed can be set to any value at the start of the test. [Learn more](random.md).
3.  Because the seed has been explicitly set, the generator is perfectly deterministic. [Learn more](random.md).
4.  All random generators have a [prepared value](prepared-values.md) alternative to easily extract initialization logic from the test bodies. They also respect the set seed if it is set before their first usage.
5.  Because no seed is set in this test, both values will generate a new integer on each execution.
6.  The current time can be set independently for each test. [Learn more](time.md).
7.  `time.clock` allows accessing a KotlinX.Datetime or Java.Time clock, that can be passed to other systems to let them access the virtual time. [Learn more](time.md).
8.  Delaying in a test will suspend the test until all services have had a chance to execute their tasks scheduled before that time. Execution order is preserved, but all delays are otherwise skipped to ensure tests finish quickly. [Learn more](time.md).
9.  Declare temporary directories that will be created at the start of any test that refers to them, and removed at the end of successful tests. [Learn more](files.md).
10. Easily refer to child files with the `/` operator. [Learn more](files.md).
