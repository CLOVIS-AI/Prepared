# Suites and tests

Prepared aims to simplify the way tests are declared. Where traditional frameworks require verbose annotation-based logic, Prepared takes advantage of Kotlin's concise syntax to remove most magic.

!!! note
    Prepared is a library to declare tests, not to execute them. A test runner is necessary; all Prepared tests must ultimately be registered to the runner. This is runner-specific and documented each runner's reference page. See [the runner list](index.md#test-runners) for more information.

In this article, we showcase how tests are declared with Prepared. Users familiar with [Kotest](https://kotest.io/), [Jest](https://jestjs.io/), [ScalaTest](https://www.scalatest.org/) or other DSL-based test frameworks will feel at home.

## Declaring tests

A test is declared by calling the [`test`](https://prepared.opensavvy.dev/api-docs/suite/opensavvy.prepared.suite/-suite-dsl/test.html) function within a suite:

```kotlin
test("This is the name of the test") {
	// This is the body of the test
}
```

Tests can `suspend` and have access to a [wide array of features](../features). You can name tests however you want.

Tests are grouped in suites. Suites are declared by calling the [`suite`](https://prepared.opensavvy.dev/api-docs/suite/opensavvy.prepared.suite/-suite-dsl/suite.html) function within another suite. Suites can be arbitrarily nested:

```kotlin
suite("Users") {
	suite("User creation") {
		test("An admin can create a user") { /* … */ }
		test("A regular user cannot create a user") { /* … */ }
	}

	suite("User listing") {
		test("An admin can list users") { /* … */ }
		test("A regular user cannot list users") { /* … */ }
		test("A regular user can access their own data") { /* … */ }
	}
}
```

Unlike Kotest's test containers, suites are lightweight and only have the purpose of declaring tests. Suites cannot `suspend` nor access any of the features of the framework directly. Suites are available on all platforms.

!!! note
    As you may have noticed, suites can only be declared within another suite. Each test runner provides a way to access a root suite into which tests can be declared. See [the runner list](index.md#test-runners) for more information.

Since work cannot be performed directly in suites, Prepared provides multiple ways of declaring operations and data that are reused between different tests: [prepared](../features/prepared-values.md) and [shared](../features/shared-values.md) values.

## Power of a DSL

Declaring tests dynamically makes many day-to-day problems trivial to solve. For example, running the same test with different values:

```kotlin hl_lines="3 4"
suite("Serializing and deserializing integers") {
	val values = listOf(1, 0, -1, Int.MAX_VALUE, Int.MIN_VALUE)
	for (value in values) {
		test("Round trip for $value") {
			check(deserialize(serialize(value)) == value)
		}
	}
}
```

This way, we can easily increase the number of edge cases we check.

We can also implement powerful patterns, such as reusing the same tests for multiple implementations of an interface:

```kotlin title="Declare an interface and its contract (also called invariants)"
interface Serializer {
	fun serialize(o: Any?): String
	fun deserialize(s: String): Any?
}

// Declare tests common to all implementations
fun SuiteDsl.serializerTests(serializer: Prepared<Serializer>) = // (1)!
	suite("Serializer invariants") {
		val values = listOf(/* … */)

		for (value in values) {
			test("Round trip for $value") {
				val impl = serializer()

				check(impl.deserialize(impl.serialize(value)) == value)
			}
		}
	}
```

1.  `Prepared<Serializer>` is a generator for type `Serializer`. When it is used, each test will instantiate its own value.
    In this example, we could have passed a `Serializer` as parameter directly because serializers are usually stateless, and thus sharing them
    between multiple tests isn't risky. However, it is a good practice to always use [prepared values](../features/prepared-values.md) when
    representing the system-under-test to protect against situations where they are not stateless.

Now that we have declared the interface, we can create an implementation:

```kotlin title="Create an implementation and ensure it corresponds to the contract" hl_lines="8 9"
class JsonSerializer : Serializer {
	// …
}

suite("Test JsonSerializer") {
	val jsonSerializer by prepared { JsonSerializer() } //(1)!

	// Import all tests for the interface
	serializerTests(jsonSerializer)

	// Add tests for this specific implementation
	test("Integers should be serialized without quotes") {
		check(jsonSerializer().serialize(5) == "5")
	}
	
	test("Strings should be serialized with quotes") {
		check(jsonSerialize().serialize("5") == "\"5\"")
	}
}
```

1.  Declares a new [prepared value](../features/prepared-values.md), which will generate a new instance of `JsonSerializer` for each test. This allows us to ensure tests can't affect each other.

No matter how many implementations we add, we never need to duplicate the tests that validate the interface itself, and we can concentrate on testing implementation-specific behavior.
