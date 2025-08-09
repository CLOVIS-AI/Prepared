# Parameterized tests

When we want to test edge cases or invariants, it is common to execute the same test, but with different data. For the sake of this example, let's say we want to test a function that checks whether a number is odd:
```kotlin
fun Int.isOdd(): Boolean =
	this % 2 == 0
```

We want to ensure that the function works for a bunch of values. A naive solution would be to test all values in a single test:
```kotlin
fun SuiteDsl.testIsOdd() {
	test("Test isOdd") {
		val even = listOf(0, 2, 4, -2, 984)
		val odd = listOf(1, 3, -1, 873)
		
		for (number in even)
			check(!number.isOdd())
		
		for (number in odd)
			check(number.isOdd())
	}
}
```

This approach works, but it has a flaw: if a test fails, it's not possible to know at a glance which other values would be successful or not. Our brain is great at recognizing patterns, so seeing all failed cases often gives insight on what could be wrong.

## Single parameter

Since Prepared is [DSL-based](../tutorials/syntax.md), we can use any language feature to programmatically declare multiple tests. For example, we can lift the loop out of the test:
```kotlin
fun SuiteDsl.testIsOdd() = suite("isOdd") {
	val even = listOf(0, 2, 4, -2, 984)
	val odd = listOf(1, 3, -1, 873)
	
	for (number in even) {
		test("$number should not be odd") {
			check(!number.isOdd())
		}
	}
	
	for (number in odd) {
		test("$number should be odd") {
			check(number.isOdd())
		}
	}
}
```

Each test will be reported as independent failures, so we can quickly get an overview of what works and what doesn't.

Instead of using loops, we can use any other language feature that helps code reuse: utility functions, the [`repeat` helper](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/repeat.html), etc.

!!! tip "Consider creating an enclosing suite"
    When declaring tests programmatically, we recommend creating an enclosing `suite` for all the generated tests, to ensure reports are easy to read. You can see this being done in the very first line of the example above.

## Multiple parameters

When we want to test combinations of multiple parameters, the previous approach can quickly become unwieldy:
```kotlin
fun SuiteDsl.foo() = suite("Foo") {
	for (a in listOf(1, 2, 3, 4, 5)) {
		for (b in listOf("b", "", "aaaaaaa", a.toString())) {
			for (c in listOf(true, false, null)) {
				test("foo $a $b $c") {
					foo(a, b, c)
				}
			}
		}
	}
}
```
The added indentation levels make the intent of the code harder to understand. Instead, we can use the [Parameterize](https://github.com/BenWoodworth/parameterize) library to simplify complex declarations.

!!! info "Configuration"
    Add a dependency on `dev.opensavvy.prepared:compat-parameterize` to use the features in this section.

    See the [reference](https://prepared.opensavvy.dev/api-docs/compat/compat-parameterize/index.html).

The previous example can be rewritten as:
```kotlin
fun SuiteDsl.foo() = suite("Foo") {
	parameterize {
		val a by parameterOf(1, 2, 3, 4, 5)
		val b by parameterOf("b", "", "aaaaaa", a.toString())
		val c by parameterOf(true, false, null)
		
		test("foo $a $b $c") {
			foo(a, b, c)
		}
	}
}
```

As you can see, this is much easier to read, and the intent of the test is conveyed much more clearly.

To learn more about this module, and the way it interacts with [prepared values](prepared-values.md), see [the reference](https://prepared.opensavvy.dev/api-docs/compat/compat-parameterize/index.html).
