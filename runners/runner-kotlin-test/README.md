# Module Execute with Kotlin-test

Execute Prepared tests alongside tests written using the standard test library.

<a href="https://search.maven.org/search?q=dev.opensavvy.prepared.runner-kotlin-test"><img src="https://img.shields.io/maven-central/v/dev.opensavvy.prepared/runner-kotlin-test.svg?label=Maven%20Central"></a>

Prepared tests are declared in subclasses of [TestExecutor][opensavvy.prepared.runner.kotlin.TestExecutor].

```kotlin
class FooTest : TestExecutor() {

	// Declare tests normally using the
	// @Test annotation
	@Test
	fun helloWorld() {
		assertEquals("Hello world", "Hello world")
	}

	// Declare tests using the Prepared syntax
	// in the 'register' function
	override fun SuiteDsl.register() {
		test("Hello world") {
			assertEquals("Hello world", "Hello world")
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
}
```

## Implementation notes

The kotlin-test library doesn't allow declaring tests dynamically.

- On the JVM, we bypass using kotlin-test, and declare tests to JUnit5 directly.
- On JS, we access the internals of kotlin-test to declare tests directly.
- On Native, we haven't found a way to declare tests dynamically—so this module doesn't support Native.

For this library to be usable in the long term, and in all platforms, we need JetBrains to provide a low-level way to
declare tests dynamically for all platforms. If you'd like to support this, please vote for [KT-46899](https://youtrack.jetbrains.com/issue/KT-46899/Dynamic-test-API).

To learn more about the platform-specific limitations, select a platform at the top of this page (below the title).
