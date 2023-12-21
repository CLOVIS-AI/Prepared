# Module Compatibility with Gradle TestKit

Write tests for Gradle plugins easily.

<a href="https://search.maven.org/search?q=dev.opensavvy.prepared.compat-gradle"><img src="https://img.shields.io/maven-central/v/dev.opensavvy.prepared/compat-gradle.svg?label=Maven%20Central"></a>

This module creates the `gradle` extension point that automatically creates a temporary directory in which the Gradle build is created.

```kotlin
test("A test that uses Gradle") {
	// Create Kotlin or Grovvy DSL files directly…
	gradle.settingsKts("""
		include("foo")
	""".trimIndent())

	gradle.rootProject.buildKts("""
		tasks.register("test") {
			doLast {
				println("Testing the root project")
			}
		}
	""".trimIndent())

	// …create multi-project builds easily…
	gradle.project("foo").buildKts("""
		tasks.register("test") {
			doLast {
				println("Testing the :foo project")
			}
		}
	""".trimIndent())

	// …start a Gradle instance in the related project…
	val result = gradle.runner()
		.withArguments("test")
		.build()

	// …assert that the output is as expected.
	result.output shouldContain "Testing the root project"
	result.output shouldContain "Testing the :foo project"
}
```

To learn more, see the [gradle][opensavvy.prepared.compat.gradle.gradle] extension point.
