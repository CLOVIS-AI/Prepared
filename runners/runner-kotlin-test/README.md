# Module Execute with Kotlin-test

Execute Prepared tests alongside tests written using the standard test library.

<a href="https://search.maven.org/search?q=dev.opensavvy.prepared.runner-kotlin-test"><img src="https://img.shields.io/maven-central/v/dev.opensavvy.prepared/runner-kotlin-test.svg?label=Maven%20Central"></a>

Prepared tests are declared in subclasses of [TestExecutor][opensavvy.prepared.runner.kotlin.TestExecutor].

```kotlin
class FooTest : TestExecutor() {

	// Declare tests normally using the
	// @Test annotation
	@Test
	fun helloWorld() {
		assertEquals("Hello world", "Hello world")
	}

	// Declare tests using the Prepared syntax
	// in the 'register' function
	override fun SuiteDsl.register() {
		test("Hello world") {
			assertEquals("Hello world", "Hello world")
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
}
```

## Configuration

### JVM project

```kotlin
// build.gradle.kts

plugins {
	kotlin("jvm") version "…"
}

dependencies {
	implementation("dev.opensavvy.prepared:runner-kotlin-test:…")
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
}

kotlin {
	jvm()
	js()

	sourceSets.commonTest.dependencies {
		implementation("dev.opensavvy.prepared:runner-kotlin-test:…")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
```

## Implementation notes and limitations

The kotlin-test library doesn't allow declaring tests dynamically on all platforms.
This module implements test execution on each platform differently.
To learn more, select a platform at the top of this page (below the title).

For this library to be usable in the long term, and in all platforms, we need JetBrains to provide a low-level way to
declare tests dynamically for all platforms. If you'd like to support this, please vote for [KT-46899](https://youtrack.jetbrains.com/issue/KT-46899/Dynamic-test-API).

Before using this runner, you may want to browse its [planned issues](https://gitlab.com/opensavvy/groundwork/prepared/-/issues/?sort=priority&state=opened&label_name%5B%5D=runner%3Akotlin-test&first_page_size=20).
