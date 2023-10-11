# Module Suite

Magicless test framework for Kotlin Multiplatform.

<a href="https://search.maven.org/search?q=dev.opensavvy.prepared.suite"><img src="https://img.shields.io/maven-central/v/dev.opensavvy.prepared/suite.svg?label=Maven%20Central"></a>

This module defines the constructs of the Prepared test framework, namely ways to declare tests, control the time, and declare test dependencies.

Prepared is only about the way tests are declared and structured: you can continue using your favorite assertion library.
We also provide compatibility modules for popular test runners, so you can benefit from Prepared without rewriting your existing tests.

## Declare tests

Tests are declared in hierarchical suites:

```kotlin
suite("Feature name") {
	suite("Feature use-case") {
		test("Test case 1") {
			// test code…
		}

		test("Test case 2") {
			// test code…
		}
	}
}
```

This has two main consequences:

- A test suite for an interface or abstract class can be reused between implementations by extracting it to an extension function on [SuiteDsl][opensavvy.prepared.suite.SuiteDsl],
- Tests can be declared programmatically, using any kind of loop.

To learn more, see [SuiteDsl][opensavvy.prepared.suite.SuiteDsl] and [TestDsl][opensavvy.prepared.suite.TestDsl].

## Coroutine-aware

Prepared tests are coroutine-aware, which provides a few interesting features:

- [Start asynchronous jobs that the test waits for][opensavvy.prepared.suite.launch],
- [Start background jobs that are automatically stopped at the end of the test][opensavvy.prepared.suite.launchInBackground],
- [Declare a cleanup job that is executed when a test ends][opensavvy.prepared.suite.cleanUp].

## Prepared values

Most testing frameworks define ways to declare “code that needs to be executed before the test happens”. This may be anything from connecting to a database, to setting up test fixtures. However, doing this:

- makes tests harder to read, as they may have implicit dependencies on some previously-running code,
- introduces shared state between states, which may affect the execution results.

Instead, we declare Prepared values: lazy operations that are explicitly invoked in tests. Their values are only computed in the context of a test, and each test gets its own execution:

```kotlin
suite("Feature name") {
	val prepareDatabase by prepared { FakeDatabase.connect() }

	test("Test case 1") {
		val database = prepareDatabase()

		database.foo()
	}

	test("Test case 2") {
		val database = prepareDatabase()

		database.bar()
	}

	test("Test case 3") {
		somethingElse()
	}
}
```

In the above example, the first and second test both get their own fake database. The third test doesn't refer to the database, so it is not instantiated.

Prepared values have a few other properties:

- Because they are executed in the context of the test that uses them, they inherit all its environment: they can `suspend`, start background jobs, register finalizers, etc.
- The [prepared][opensavvy.prepared.suite.prepared] builder can be called anywhere, including at the file top-level, which is convenient for declaring test fixtures that are used in many places.
- Prepared values are transitive: prepared values can themselves refer to other prepared values, etc.
- A single prepared value can be referenced multiple times in the same test and will always return the same value. For example, a prepared value that gives a random integer will always give the same one in a given test.

To learn more, see [prepared][opensavvy.prepared.suite.prepared].

## Control the time

We often write algorithms that depend on the current time. Sometimes, we discover bugs that only happen at rare times, for example during daylight savings transitions, when the year changes, or in leap years.

To avoid these issues, it is recommended to inject some kind of clock into all algorithms that depend on the current time, or that need to measure how time passes.

Inside Prepared tests, the [time][opensavvy.prepared.suite.time] accessor exposes methods to control how time passes:

- Control tasks started in the future,
- Skip delays between tasks,
- Set the current time.

To learn more, see [time][opensavvy.prepared.suite.time].
Compatibility with other time management libraries (e.g. KotlinX.Datetime) are provided as optional dependencies.
