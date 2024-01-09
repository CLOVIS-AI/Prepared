# Module Compatibility with Parameterize

Concise DSL for parameterized tests, using [Parameterize](https://github.com/BenWoodworth/Parameterize).

<a href="https://search.maven.org/search?q=dev.opensavvy.prepared.compat-parameterize"><img src="https://img.shields.io/maven-central/v/dev.opensavvy.prepared/compat-parameterize.svg?label=Maven%20Central"></a>

Prepared allows to easily declare multiple variants of the same test, however, nesting is necessary:

```kotlin
suite("A test suite") {
	val middle = "substring"
	for (prefix in listOf("", "prefix-")) {
		for (suffix in listOf("", "-suffix")) {
			val string = "$prefix$middle$suffix"
			test("$string contains $middle") {
				assertContains(middle, string)
			}
		}
	}
}
```

Using Parameterize removes the nesting:

```kotlin
suite("A test suite") {
	parameterize {
		val middle = "substring"
		val prefix by parameterOf("", "prefix-")
		val suffix by parameterOf("", "-suffix")

		val string = "$prefix$middle$suffix"

		test("$string contains $middle") {
			assertContains(middle, string)
		}
	}
}
```

## Prepared values

[Prepared values][opensavvy.prepared.suite.Prepared] allow declaring lazily-computed test fixtures. Using this module, they can be used as test parameters as well. Depending on the situation, two syntaxes are available.

When prepared values are heterogeneous, declare them then combine them as parameters:

```kotlin
suite("A test suite") {
	parameterize {
		val one by prepared { 1 }
		val other by prepared { random.nextInt() }
		val number by parameterOf(one, other)

		test("Test $number") {
			assertTrue(number().toString().isNotEmpty())
		}
	}
}
```

When they are constructed by [transforming another parameter][opensavvy.prepared.compat.parameterize.prepare], declare the parameter first:

```kotlin
suite("A test suite") {
	parameterize {
		val number by parameterOf(0, 1, Int.MAX_VALUE, -99)
			.prepare { expensiveOperation(it) }

		test("Test $number") {
			assertTrue(number().successful)
		}
	}
}
```
