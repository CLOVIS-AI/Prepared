# Overview

Prepared adds many helpers to facilitate writing tests. To learn about them, either select them in the menu on the left of this page, or click on one of the annotations in this complete example:

??? warning "This example is not idiomatic!"
    This example is written specifically to showcase all features in a single place. It is not considered idiomatic.
    For more information, visit [the best practices section](../practices/overview.md).

??? note "Assertion library"
    This example uses the Kotest assertion library.
    [Learn more about assertion libraries](../tutorials/getting-started.md#assertion-libraries).

```kotlin
fun SuiteDsl.testUsers( // (1)!
	users: Prepared<UserService>,
) {
	suite("Example") { // (2)!
		test("Hello world") { // (3)!
			"Hello world" shouldBe "Hello world"
		}

		test("Another test") {
			(2 + 2) shouldBe 4
		}
	}
}
```

1. The `SuiteDsl` receiver enables the declaration of tests and suites that are compatible with any test runner.
   To learn more, see [the getting started section](../tutorials/getting-started.md#test-runners).
2. The `suite` function creates a nested container to organize related tests into a single unit.
   [Learn more](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/suite/opensavvy.prepared.suite/-suite-dsl/suite.html).
3. The `test` function declares the existence of a test.
   [Learn more](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/suite/opensavvy.prepared.suite/-suite-dsl/test.html).
