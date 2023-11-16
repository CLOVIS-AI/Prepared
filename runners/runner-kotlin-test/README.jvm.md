# Module Execute with Kotlin-test

Execute Prepared tests alongside tests written using the standard test library.

<a href="https://search.maven.org/search?q=dev.opensavvy.prepared.runner-kotlin-test"><img src="https://img.shields.io/maven-central/v/dev.opensavvy.prepared/runner-kotlin-test.svg?label=Maven%20Central"></a>

## How it works

We use the [JUnit5 dynamic test API][org.junit.jupiter.api.DynamicNode] to declare tests.

## Limitations

This is only compatible with JUnit5, not JUnit4.
To ensure your tests run, remember to configure Gradle:

```kotlin
// For kotlin("jvm")
tasks.test {
	useJUnitPlatform()
}
```

```kotlin
// For kotlin("multiplatform")
kotlin {
	jvm {
		testRuns.named("test") {
			executionTask.configure {
				useJUnitPlatform()
			}
		}
	}
}
```
