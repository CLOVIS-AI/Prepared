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

## Setup

Follow the [Kotest setup guide](https://kotest.io/docs/framework/project-setup.html), simply adding a dependency on this
module as well.

## Limitations

Kotest expects nested suites to be `suspend`. Because of this, Kotest cannot allow nested suites on Kotlin/JS.
Prepared supports nested suites on all platforms; they are un-nested automatically when executing with Kotest.

Kotest doesn't expose the underlying dispatcher from [KotlinX.Coroutines.test](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/). Prepared needs access to it to implement the [backgroundScope][opensavvy.prepared.suite.backgroundScope] and [time control][opensavvy.prepared.suite.time]. This forces us to declare our own scheduler.
This is broken on Kotlin/JS at the moment, please see [#12](https://gitlab.com/opensavvy/prepared/-/issues/12), we welcome contributions!
