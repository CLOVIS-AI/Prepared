# Module Execute with Kotest

Execute Prepared tests alongside tests written using Kotest.

<a href="https://search.maven.org/search?q=dev.opensavvy.prepared.runner-kotest"><img src="https://img.shields.io/maven-central/v/dev.opensavvy.prepared/runner-kotest.svg?label=Maven%20Central"></a>

Prepared tests are declared using the helper [preparedSuite][opensavvy.prepared.runner.kotest.preparedSuite].

```kotlin
class FooTest : StringSpec({
	// Declare tests normally using the Kotest syntax
	"Hello world" {
		"Hello world" shouldBe "Hello world"
	}

	// Declare tests using the Prepared syntax
	// using the 'preparedSuite' function
	preparedSuite {
		test("Hello world") {
			"Hello world" shouldBe "Hello world"
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
})
```

If you do not plan on mixing vanilla Kotest tests with your Prepared tests, you can also use the [PreparedSpec][opensavvy.prepared.runner.kotest.PreparedSpec]:

```kotlin
class FooTest : PreparedSpec({
	test("Hello world") {
		"Hello world" shouldBe "Hello world"
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
})
```

## Configuration

### JVM project

```kotlin
// build.gradle.kts

plugins {
	kotlin("jvm") version "…"
}

dependencies {
	implementation("dev.opensavvy.prepared:runner-kotest:…")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
```

### Multiplatform project

```kotlin
// build.gradle.kts

plugins {
	kotlin("multiplatform") version "…"

	// ⚠ Without the Kotest plugin, tests for some platforms 
	// may not run, without warnings ⚠
	// https://plugins.gradle.org/plugin/io.kotest.multiplatform
	id("io.kotest.multiplatform") version "…"
}

kotlin {
	jvm()
	js()
	// …

	sourceSets.commonTest.dependencies {
		implementation("dev.opensavvy.prepared:runner-kotest:…")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
```

## Features

This runner automatically enables some Kotest features:

- Always uses the KotlinX.Coroutine dispatcher,
- KotlinX.Coroutines debug probes are always enabled,
- [Ignored tests][opensavvy.prepared.suite.config.Ignored] are mapped to Kotest ignored tests,
- [Test tags][opensavvy.prepared.suite.config.Tag] are mapped to Kotest test tags,
- [Focus and Bang](https://kotest.io/docs/framework/conditional/conditional-tests-with-focus-and-bang.html) are supported. Unlike regular Kotest, both features also work with tests declared in suites.

## Limitations

Kotest expects nested suites to be `suspend`. Because of this, Kotest cannot allow nested suites on Kotlin/JS.
Prepared supports nested suites on all platforms; they are un-nested automatically when executing with Kotest.

Before using this runner, you may want to browse its [planned work](https://gitlab.com/opensavvy/groundwork/prepared/-/issues/?sort=priority&state=opened&label_name%5B%5D=runner%3Akotest&first_page_size=20).
