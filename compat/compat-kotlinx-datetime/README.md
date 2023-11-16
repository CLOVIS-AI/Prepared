# Module Compatibility with KotlinX.Datetime

Control the virtual time during tests using KotlinX.Datetime.

<a href="https://search.maven.org/search?q=dev.opensavvy.prepared.compat-kotlinx-datetime"><img src="https://img.shields.io/maven-central/v/dev.opensavvy.prepared/compat-kotlinx-datetime.svg?label=Maven%20Central"></a>

Builds upon the [virtual time control][opensavvy.prepared.suite.time] available out-of-the-box to allow
[instancing clocks][opensavvy.prepared.compat.kotlinx.datetime.clock], [setting the current time][opensavvy.prepared.compat.kotlinx.datetime.set] or [waiting for a given time][opensavvy.prepared.compat.kotlinx.datetime.delayUntil].

## Example

We want to test a fictional `Scheduler` implemented using [KotlinX.Coroutines](https://kotlinlang.org/docs/coroutines-guide.html).
The scheduler has been implemented in a way that allows to inject the clock and the coroutine context.

First, the scheduler requires access to some kind of database.
We [prepare][opensavvy.prepared.suite.prepared] the connection to avoid copy-pasting it in each test, while still
ensuring each test gets its own instance.

```kotlin
val prepareDatabase by prepared {
	Database.connect()
}
```

To allow writing multiple tests using the same scheduler, we also declare it as a prepared value.
We can inject the database using the previous prepared value.
To let the scheduler access the virtual time, we inject the [virtual time clock][opensavvy.prepared.compat.kotlinx.datetime.clock].
To ensure the scheduler can start coroutines with delay-skipping that the test waits for, we inject the [foreground coroutine scope][opensavvy.prepared.suite.foregroundScope].

```kotlin
val prepareScheduler by prepared {
	Scheduler(
		database = prepareDatabase(),
		clock = time.clock,
		coroutineContext = foregroundScope,
	)
}
```

Now, we can use the helper functions [to set the current time][opensavvy.prepared.compat.kotlinx.datetime.set]
and to [wait until a specific time][opensavvy.prepared.compat.kotlinx.datetime.delayUntil].

```kotlin
test("A test that uses the time") {
	time.set("2023-11-08T12:00:00Z")

	val scheduler = prepareScheduler()

	var executed = false
	scheduler.scheduleAt("2023-11-08T12:05:00Z") {
		executed = true
	}

	time.delayUntil("2023-11-08T12:06:00Z")
	executed shouldBe true
}
```
