# Module Compatibility with Arrow

Helpers to fail tests when a function raises and to use resources.

<a href="https://search.maven.org/search?q=dev.opensavvy.prepared.compat-arrow"><img src="https://img.shields.io/maven-central/v/dev.opensavvy.prepared/compat-arrow.svg?label=Maven%20Central"></a>

# Package opensavvy.prepared.compat.arrow.core

Helpers to integrate Arrow's Raise DSL into tests.

Let's assume we want to test a function which raises when it receives a negative number:

```kotlin
data object NegativeSquareRoot

context(Raise<NegativeSquareRoot>)
fun sqrt(value: Double): Double {
	ensure(value >= 0) { NegativeSquareRoot }
	return kotlin.math.sqrt(value)
}
```

We can write a test that ensures the function does not raise, using [failOnRaise][opensavvy.prepared.compat.arrow.core.failOnRaise]:

```kotlin
test("√4 does not raise") {
	failOnRaise {
		sqrt(4.0)
	} shouldBe 2.0
}
```

We can write a test that ensures the function does raise, using [assertRaises][opensavvy.prepared.compat.arrow.core.assertRaises] or [assertRaisesWith][opensavvy.prepared.compat.arrow.core.assertRaisesWith]:

```kotlin
test("√-1 raises") {
	// assert raises a specific value
	assertRaises(NegativeSquareRoot) {
		sqrt(-1.0)
	}

	// assert raises any value of a specific type
	assertRaisesWith<NegativeSquareRoot> {
		sqrt(-1.0)
	}
}
```

# Package opensavvy.prepared.compat.arrow.coroutines

Helpers to use Arrow's Resource DSL in tests.

Let's assume that we want to test a service we own that is already encapsulated in a resource within your production code:

```kotlin
val userProcessor: Resource<UserProcessor> = resource({
	UserProcessor().also { it.start() }
}) { p, _ -> p.shutdown() }

val dataSource: Resource<DataSource> = resource({
	DataSource().also { it.connect() }
}) { ds, exitCase ->
	println("Releasing $ds with exit: $exitCase")
	withContext(Dispatchers.IO) { ds.close() }
}

val service: Resource<Service> = resource {
	Service(dataSource.bind(), userProcessor.bind())
}
```

We can use [asPrepared][opensavvy.prepared.compat.arrow.coroutines.asPrepared] to convert that resource into Prepared's native [prepared values][opensavvy.prepared.suite.Prepared]:

```kotlin
val preparedService by service.asPrepared()

test("create() should not return null") {
	check(preparedService().create() != null)
}
```
