# Module Execute with Kotest

Execute Prepared tests alongside tests written using Kotest.

<a href="https://search.maven.org/search?q=dev.opensavvy.prepared.runner-kotest"><img src="https://img.shields.io/maven-central/v/dev.opensavvy.prepared/runner-kotest.svg?label=Maven%20Central"></a>

Prepared tests are declared using the helper [preparedSuite][opensavvy.prepared.runner.kotest.preparedSuite].

```kotlin
class FooTest : StringSpec({
	// Declare tests normally using the Kotest syntax
	"Hello world" {
		"Hello world" shouldBe "Hello world"
	}

	// Declare tests using the Prepared syntax
	// using the 'preparedSuite' function
	preparedSuite {
		test("Hello world") {
			"Hello world" shouldBe "Hello world"
		}

		suite("A group of related tests") {
			test("Control the time") {
				println("Current time: ${time.nowMillis}ms")
			}

			test("Control randomness") {
				random.setSeed(1)

				println("Random value: ${random.nextInt()}")
			}
		}
	}
})
```

If you do not plan on mixing vanilla Kotest tests with your Prepared tests, you can also use the [PreparedSpec][opensavvy.prepared.runner.kotest.PreparedSpec]:

```kotlin
class FooTest : PreparedSpec({
	test("Hello world") {
		"Hello world" shouldBe "Hello world"
	}

	suite("A group of related tests") {
		test("Control the time") {
			println("Current time: ${time.nowMillis}ms")
		}

		test("Control randomness") {
			random.setSeed(1)

			println("Random value: ${random.nextInt()}")
		}
	}
})
```

## Setup

Follow the [Kotest setup guide](https://kotest.io/docs/framework/project-setup.html), simply adding a dependency on this
module as well.

## Limitations

Kotest expects nested suites to be `suspend`. Because of this, Kotest cannot allow nested suites on Kotlin/JS.
Prepared supports nested suites on all platforms; they are un-nested automatically when executing with Kotest.
