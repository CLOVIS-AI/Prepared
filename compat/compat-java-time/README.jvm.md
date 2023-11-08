# Module Compatibility with java.time

Control the virtual time during tests using the `java.time` package.

<a href="https://search.maven.org/search?q=dev.opensavvy.prepared.compat-java-time"><img src="https://img.shields.io/maven-central/v/dev.opensavvy.prepared/compat-java-time.svg?label=Maven%20Central"></a>

Builds upon the [virtual time control][opensavvy.prepared.suite.time] available out-of-the-box to allow
[instancing clocks][opensavvy.prepared.compat.java.time.clockJava], [setting the current time][opensavvy.prepared.compat.java.time.set] or [waiting for a given time][opensavvy.prepared.compat.java.time.delayUntil].

## Example

We want to test a Java class that makes computations based on the current time.

To allow writing multiple tests using the same class, we declare it as a [prepared value][opensavvy.prepared.suite.prepared].
To ensure it has access to the virtual time, we inject the [Java clock][opensavvy.prepared.compat.java.time.clockJava].

```kotlin
val prepareComputer by prepared {
	TimeComputer(
		clock = time.clockJava,
	)
}
```

Now, we can use the helper functions [to set the current time][opensavvy.prepared.compat.java.time.set]
and to [wait until a specific time][opensavvy.prepared.compat.java.time.delayUntil].

```kotlin
test("A test that uses the time") {
	time.set(Instant.parse("2023-11-08T12:00:00Z"))

	val computer = prepareComputer()

	computer.isInTheFuture("2023-11-08T11:00:00Z") shouldBe false
}
```
