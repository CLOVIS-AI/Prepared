# Getting started

Test frameworks are split into multiple tools:

- **Test runners** execute tests,
- **Structure libraries** provide high-level DSLs for humans, and convert them into low-level instructions for the runner,
- **Assertion libraries** provide helpers to compare values and check the state of a system.

Prepared is primarily a structure library. To write tests, you will need a test runner and (optionally) an assertion library.

## Test runners

A test runner is responsible to communicate with the rest of the build tooling: executing tests when requested, executing tests in parallel or not, reporting the results to the build tool or IDE, selecting which tests to execute…

Prepared doesn't include a built-in test runner.

### Kotlin-test

The `kotlin-test` runner is based on the [Kotlin standard test library](https://kotlinlang.org/api/core/kotlin-test/).
To decide whether it is appropriate for your project, see the available platforms and see the required configuration, see [the reference](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/runners/runner-kotlin-test/index.html).

### Kotest

The `kotest` runner is based on the [Kotest framework](https://kotest.io/docs/framework/framework.html).
To decide whether it is appropriate for your project, see the available platforms and see the required configuration, see [the reference](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/runners/runner-kotest/index.html).

### Without a runner

Prepared can also be accessed without any runner. This mode allows declaring tests, but not running them.
This is useful when creating a test utility module that is imported into other test modules.
By not depending on any runner, the test utility module can be imported into a project that uses any runner.

To do this, simply declare a dependency on `dev.opensavvy.prepared:suite`. To learn more, see [the reference](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/suite/index.html).

Test suites can be exported as top-level functions:

```kotlin
fun SuiteDsl.testUsers(users: Prepared<UserService>) = suite("User service $users") {
	test("foo") {
		// …
	}
}
```

To learn more, visit [the feature list](../features/overview.md).

## Assertion libraries

Assertion libraries provide utilities for verifying the state of data structures. Prepared is compatible with all assertion libraries that throw exceptions (all the ones we know).

Here are a few popular choices:

=== "Kotlin-test"

    Part of the [Kotlin standard test library](https://kotlinlang.org/api/core/kotlin-test/).

    **Usage**

    ```kotlin
    // Equality check
    assertEquals("Hello world", "Hello world")

    // Collection check
    assertContains(listOf(1, 2, 3), 2)

    // Thrown exceptions
    assertFailsWith<IllegalStateException> { error("foo") }
    ```

    Assertion grouping is not supported.

    **Configuration**

    Add a dependency on `org.jetbrains.kotlin:kotlin-test`.

    !!! info
        This library is already included if you use the `kotlin-test` runner.

=== "Kotest Assertions"

    [Kotest Assertions](https://kotest.io/docs/assertions/assertions.html) is a part of the Kotest framework.
    Kotest provides assertion helpers for many libraries, called "matcher modules".

    **Usage**

    ```kotlin
    // Equality check
    "Hello world" shouldBe "Hello world"

    // Collection check
    listOf(1, 2, 3) shouldContain 2

    // Thrown exceptions
    shouldThrow<IllegalStateException> { error("foo") }

    // Assertion grouping
    assertSoftly(Person("foo", 12)) {
        name shouldBe "foo"
        age shouldBe 12
    }
    ```

    **Configuration**

    Add a dependency on `io.kotest:kotest-assertions-core`.
    
    To use additional matchers modules, see their respective documentation.

    !!! info
        This library is already included if you use the `kotest` runner.

=== "Strikt"

    [Strikt](https://strikt.io/) is a Kotlin library inspired by AssertJ.

    **Usage**

    ```kotlin
    // Equality check
    expectThat("Hello world") isEqualTo "Hello world"

    // Collection check
    expectThat(listOf(1, 2, 3))
        .contains(2)

    // Thrown exceptions
    expectCatching { error("foo") }
        .isFailure()
        .isA<IllegalStateException>()

    // Assertion grouping
    expectThat(Person("foo", 12)) {
        get { name } isEqualTo "foo"
        get { age } isEqualTo 12
    }
    ```

    **Configuration**

    Add a dependency on `io.strikt:strikt-core`.

    !!! warning
        Strikt only supports the JVM platform.

=== "AssertK"

    [AssertK](https://github.com/willowtreeapps/assertk) is a Kotlin library inspired by AssertJ.

    **Usage**

    ```kotlin
    // Equality check
    assertThat("Hello world").isEqualTo("Hello world")

    // Collection check
    assertThat(listOf(1, 2, 3))
        .containsAtLeast(2)

    // Thrown exceptions
    assertFailure { error("foo") }
        .isInstanceOf<IllegalStateException>()

    // Assertion grouping
    assertThat(Person("foo", 12))
        .prop(Person::name).isEqualTo("foo")
        .prop(Person::age).isEqualTo(12)
    ```

    **Configuration**

    Add a dependency on `com.willowtreeapps.assertk:assertk`.

=== "Truthish"

    [Truthish](https://github.com/varabyte/truthish) is a Kotlin library inspired by [Google Truth](https://github.com/google/truth).

    **Usage**

    ```kotlin
    // Equality check
    assertThat("Hello world").isEqualTo("Hello world")

    // Collection check
    assertThat(listOf(1, 2, 3))
        .contains(2)

    // Thrown exceptions
    assertThrows<IllegalStateException> {
        error("foo")
    }
    ```

    **Configuration**

    Add a dependency on `com.varabyte.truthish:truthish`.
