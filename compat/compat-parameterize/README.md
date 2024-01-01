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
