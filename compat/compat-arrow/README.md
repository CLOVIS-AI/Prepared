# Module Compatibility with Arrow

Helpers to fail tests when a function raises.

<a href="https://search.maven.org/search?q=dev.opensavvy.prepared.compat-arrow"><img src="https://img.shields.io/maven-central/v/dev.opensavvy.prepared/compat-arrow.svg?label=Maven%20Central"></a>

### Example

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
