# Module Execute with TestBalloon

Execute Prepared tests with the [TestBalloon framework](https://github.com/infix-de/testBalloon).

<a href="https://search.maven.org/search?q=dev.opensavvy.prepared.runner-testballoon"><img src="https://img.shields.io/maven-central/v/dev.opensavvy.prepared/runner-testballoon.svg?label=Maven%20Central"></a>

## Configuration

Add the TestBalloon Gradle plugin and a dependency on this library:
```kotlin
plugins {
	kotlin("multiplatform")
	id("de.infix.testBalloon") version "VERSION" //(1)
}

kotlin {
	// …
	
	sourceSets.commonTest.dependencies {
		implementation("dev.opensavvy.prepared:runner-testballoon:VERSION") //(2)
	}
}
```

1. [List of TestBalloon versions](https://github.com/infix-de/testBalloon/releases)
2. [List of Prepared versions](https://gitlab.com/opensavvy/groundwork/prepared/-/releases)

## Usage

To declare a Prepared suite using the TestBalloon runner, simply declare a top-level variable initialized using the [`preparedSuite`][opensavvy.prepared.runner.testballoon.preparedSuite] helper:
```kotlin
val MyTestSuite by preparedSuite {
	test("Hello world!") {
		assert(3 == 3)
	}
	
	suite("My suite") {
		test("First") { /* … */ }
		test("Second") { /* … */ }
	}
}
```

If you are already using TestBalloon and want to use Prepared features within an existing suite, use the [`withPrepared`][opensavvy.prepared.runner.testballoon.withPrepared] helper:
```kotlin
val MyMixedTestSuite by testSuite {
	// Here, we're using TestBalloon syntax
	test("Foo") { /* … */ }
	testSuite("Bar") { /* … */ }
	
	withPrepared {
		// Here, we're using Prepared syntax
		test("Foo") { /* … */ }
		suite("Bar") { /* … */ }
	}
}
```

## IntelliJ plugin

The [TestBalloon IntelliJ plugin](https://plugins.jetbrains.com/plugin/27749-testballoon) can be configured to recognize Prepared tests and suites.

Once you installed the plugin, open “[File | Settings | Tools | TestBalloon](jetbrains://idea/settings?name=Tools--TestBalloon)“, open “Discovery settings”. In the field “Test discoverable annotations“, add `opensavvy.prepared.suite.annotations.TestEntrypoint` to the end, using a space character to separate it with existing values. Save your settings. You may need to reload your build.

You should see the typical green triangles in the margin of Prepared tests and suites, allowing to run or debug a single test.
