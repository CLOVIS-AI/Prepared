---
template: home.html
---

# Welcome!

OpenSavvy Prepared helps declare isolated tests quickly with an expressive DSL, that can run on any Kotlin platform.

## Let's take a sneak peek

The simplest test is declared as a simple function:

```kotlin
test("Foo") {
	/* … */
}
```

Tests can be organized into suites, which can be nested any number of times:

```kotlin
suite("My test suite") {
	test("A first test") { /* … */ }
	
	suite("A nested suite") {
		test("A second test") { /* … */ }
		test("A third test") { /* … */ }
	}
}
```

Additionally, Prepared exposes many advanced features:

- [Isolated test fixtures](features/prepared-values.md),
- [Time control](features/time.md),
- [Background task management](features/async.md)¸
- [Randomness control](features/random.md),
- [Temporary filesystems](features/files.md),
- …and [more](features/index.md).

## Prepared isn't a test runner

The goal of Prepared is to simplify how we declare tests, how we go from a thought to code. Test runners are libraries that execute test batteries and report results to your build system. Prepared isn't a test runner, but [it is compatible with a few existing ones](tutorials/index#test-runners).

## Prepared isn't an assertion library

Assertion libraries provide utilities to compare values. Popular choices are [Kotlin.test](https://kotlinlang.org/api/latest/kotlin.test/), [Kotest Assertions](https://kotest.io/docs/assertions/assertions.html), [Strikt](https://strikt.io/), [AssertK](https://github.com/willowtreeapps/assertk), [Atrium](https://atriumlib.org)… just use the one you prefer!

Instead of any specific assertion libraries, we recommend using [Power Assert](https://kotlinlang.org/docs/power-assert.html), which is able to generate good error messages from regular Kotlin code, without needing an assertion library at all.

## Prepared isn't an IntelliJ plugin (yet?)

Prepared is a simple Kotlin library. It doesn't have a Gradle plugin, nor does it have an IntelliJ plugin. Test are reported by the runner, so your IDE can display the test report. However, IntelliJ doesn't know which lines are tests or not, so it cannot display the small green triangle to select which tests to execute. 
