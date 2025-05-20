# Date and time

Tests run in virtual time: a fixed timescale which is controlled by the developer.

The current virtual time can be accessed using the [`time.nowMillis`](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/suite/opensavvy.prepared.suite/now-millis.html) property.
To control the current date as proper datetime types, or to inject a controllable clock into other services, see [Using the virtual time](#using-the-virtual-date-and-time).

## Delay-skipping

Time-sensitive algorithms are often difficult to test, because we want tests to execute quickly, which is often incompatible.
For example, testing that a notification is sent a month before disabling an account, or that a ping is sent every 10 seconds.

Through the use of the virtual time, we can control how the algorithm *thinks* the time is passing, allowing us to trigger these cases easily. However, a major risk of direct time control is that we risk changing the order of events, which might mean that our test doesn't represent the real world anymore.

To decrease these risks, the time is directly controlled by `delay` calls.

For example, in this example:
```kotlin
test("Observing the passing of time") {
	println("Hello world!")
	delay(1000)
	println("…After one second")
}
```
When running this example, we notice that it finishes in a few milliseconds, and not a full second. This is because delays are skipped. Since the virtual time is fixed, starts at `0`ms, and only advances through `delay` calls, we know that at the end of this test it is always equal to exactly `1` second.

This determinism allows us to precisely control algorithms that could otherwise be nondeterministic in real time:
```kotlin
test("Deterministic example") {
	launchInBackground { //(1)!
		while (isActive) {
			delay(10)
			println("10ms have passed")
		}
	}
	
	println("Test start")
	delay(25)
	println("Middle of the test")
	delay(10)
	println("Test end")
}
```

1.  `launchInBackground` starts a background service that is killed at the end of the test. See [asynchronous helpers](async.md).

This test is guaranteed to always result in the following trace:
```text
Test start
10ms have passed
10ms have passed
Middle of the test
10ms have passed
Test end
```

As we can see, the execution can be deterministically tested. Systems that wait for a very long time can be tested as well, since the entire waiting time can be skipped. Complex edge cases and event orderings can be triggered easily. 

!!! warning
    Delay-skipping only works in specific dispatchers. The coroutine scope in which tests are executed, as well as the ones created by the [asynchronous helpers](async.md) are already configured with delay-skipping.

    However, other dispatchers, most notably `Dispatchers.IO` and `Dispatchers.Main`, do not use delay-skipping. If calling `delay` within a `withContext(Dispatchers.IO)` call, the `delay` will wait in real time.

    This is one of the reasons why real code should never refer to specific dispatchers directly. Instead, services that must start coroutines should accept a `CoroutineScope` or `CoroutineContext` parameter. Test code can initialize them with:

    - If the service accepts a `CoroutineScope`, see [the asynchronous helpers section](async.md#controlling-the-execution-of-external-services).
    - If the service accepts a `CoroutineContext`, see [`time.scheduler`](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/suite/opensavvy.prepared.suite/-time/scheduler.html).

### Interaction with fixtures

[Prepared values](prepared-values.md) run in the context of each test. All time-control features are available within prepared values.

[Shared values](shared-values.md) are only executed once. The `time` accessor is not available within them. We do not recommend using `delay` within them, since it will only affect the virtual time of the very first test to run, which is not deterministic (it will be different if you enable parallel execution or if you run a single test).

### Interaction with asynchronous operations

[Foreground and background operations](async.md) affect the virtual time exactly in the same way as accessing it directly within a test's body.

## Measuring the passing of time

It is possible to measure how long an operation takes in virtual time or in real time.

### Measuring in virtual time

See [`time.source`](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/suite/opensavvy.prepared.suite/-time/source.html):

```kotlin
test("Measure how long an operation takes in virtual time") {
    val duration = time.source.measureTime {
        delay(3.minutes)
    }

    check(duration == 3.minutes)
}
```

Measuring how long an operation takes in virtual time is rarely useful: since virtual time only advances naturally when `delay` is called, this is equivalent to counting all calls to `delay`.

### Measuring in real time

To measure how long an operation takes in real time, see [`measureTime`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/measure-time.html):

```kotlin
import kotlin.time.measureTime

test("Measure how long an operation takes in real time") {
    val duration = measureTime {
        delay(3.minutes)
    }

    check(duration < 10.milliseconds) // because 'delay' is skipped
}
```

## Using the virtual date and time

Often, `time.nowMillis` is insufficient: we want to trigger algorithms at specific dates in time, for example to check that an algorithm behaves correctly even at midnight on New Year's. Prepared offers compatibility modules to generate datetime objects from popular libraries.

=== "Kotlin.time"

    The new `kotlin.time` package is supported by default with no additional dependencies.

=== "KotlinX.Datetime"

    Add a dependency on `dev.opensavvy.prepared:compat-kotlinx-datetime`.
    See the [reference](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/compat/compat-kotlinx-datetime/index.html).

=== "Java Time"

    Add a dependency on `dev.opensavvy.prepared:compat-java-time`.
    See the [reference](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/compat/compat-java-time/index.html).

### Accessing the current time

=== "Kotlin.time"

    [`time.now`](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/suite/opensavvy.prepared.suite/now.html) can be used to access the current time as an `Instant`:

    ```kotlin
    test("Access the current time") {
        println(time.now)  // 1970-01-01T00:00:00Z
        delay(2.hours)
        println(time.now)  // 1970-01-01T02:00:00Z
    }
    ```

=== "KotlinX.Datetime"

    [`time.now`](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/compat/compat-kotlinx-datetime/opensavvy.prepared.compat.kotlinx.datetime/now.html) can be used to access the current time as an `Instant`:

    ```kotlin
    test("Access the current time") {
        println(time.now)  // 1970-01-01T00:00:00Z
        delay(2.hours)
        println(time.now)  // 1970-01-01T02:00:00Z
    }
    ```

=== "Java Time"

    [`time.nowJava`](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/compat/compat-java-time/opensavvy.prepared.compat.java.time/now-java.html) can be used to access the current time as an `Instant`:

    ```kotlin
    test("Access the current time") {
        println(time.nowJava)  // 1970-01-01T00:00:00Z
        delay(2.hours)
        println(time.nowJava)  // 1970-01-01T02:00:00Z
    }
    ```

### Setting the time at the start of a test

=== "Kotlin.time"

    By default, the virtual time is set to UNIX Time 0, January 1st, 1970 at 00:00:00. To set the initial time to another initial date, use [`time.set`](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/suite/opensavvy.prepared.suite/set.html):

    ```kotlin
    test("Set the initial time of a test") {
        time.set("2024-12-31T05:00:00Z")
        
        delay(4.hours)
        println(time.now)    // 2024-12-31T09:00:00Z
    }
    ```

=== "KotlinX.Datetime"

    By default, the virtual time is set to UNIX Time 0, January 1st, 1970 at 00:00:00. To set the initial time to another initial date, use [`time.set`](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/compat/compat-kotlinx-datetime/opensavvy.prepared.compat.kotlinx.datetime/set.html):

    ```kotlin
    test("Set the initial time of a test") {
        time.set("2024-12-31T05:00:00Z")
        
        delay(4.hours)
        println(time.now)    // 2024-12-31T09:00:00Z
    }
    ```

=== "Java Time"

    By default, the virtual time is set to UNIX Time 0, January 1st, 1970 at 00:00:00. To set the initial time to another initial date, use [`time.set`](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/compat/compat-java-time/opensavvy.prepared.compat.java.time/set.html):

    ```kotlin
    test("Set the initial time of a test") {
        time.set(Instant.parse("2024-12-31T05:00:00Z"))
        
        delay(4.hours)
        println(time.nowJava)    // 2024-12-31T09:00:00Z
    }
    ```


### Injecting the virtual time into the system-under-test

=== "Kotlin.time"

    Algorithms we want to test need to have access to the virtual time. Traditionally, this is done by accepting a `Clock` in the algorithm's constructor. We can generate such a clock with [`time.clock`](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/suite/opensavvy.prepared.suite/clock.html):

    ```kotlin
    test("Test an algorithm that performs time-sensitive operations") {
        startAlgorithm(time.clock)
        delay(2.days + 5.hours)
        endAlgorithm()

        // …check the final state…
    }
    ```    

=== "KotlinX.Datetime"

    Algorithms we want to test need to have access to the virtual time. Traditionally, this is done by accepting a `Clock` in the algorithm's constructor. We can generate such a clock with [`time.clock`](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/compat/compat-kotlinx-datetime/opensavvy.prepared.compat.kotlinx.datetime/clock.html):

    ```kotlin
    test("Test an algorithm that performs time-sensitive operations") {
        startAlgorithm(time.clock)
        delay(2.days + 5.hours)
        endAlgorithm()

        // …check the final state…
    }
    ```

=== "Java Time"

    Algorithms we want to test need to have access to the virtual time. Traditionally, this is done by accepting a `Clock` in the algorithm's constructor. We can generate such a clock with [`time.clockJava`](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/compat/compat-java-time/opensavvy.prepared.compat.java.time/clock-java.html):

    ```kotlin
    test("Test an algorithm that performs time-sensitive operations") {
        startAlgorithm(time.clockJava)
        delay(2.days + 5.hours)
        endAlgorithm()

        // …check the final state…
    }
    ```

### Waiting until a specific time

=== "Kotlin.time"

    Instead of using `delay` to wait for a specific duration, we can also use [`delayUntil`](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/suite/opensavvy.prepared.suite/delay-until.html) to wait until a specific instant.

    ```kotlin
    test("Observe what happens when the year changes") {
        time.set("2024-12-31T05:00:00Z")

        startAlgoithm(time.clock)
        time.delayUntil("2025-01-01T01:00:00Z")
        endAlgorithm()

        // …check the final state…
    }
    ```

=== "KotlinX.Datetime"

    Instead of using `delay` to wait for a specific duration, we can also use [`delayUntil`](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/compat/compat-kotlinx-datetime/opensavvy.prepared.compat.kotlinx.datetime/delay-until.html) to wait until a specific instant.

    ```kotlin
    test("Observe what happens when the year changes") {
        time.set("2024-12-31T05:00:00Z")

        startAlgoithm(time.clock)
        time.delayUntil("2025-01-01T01:00:00Z")
        endAlgorithm()

        // …check the final state…
    }
    ```

=== "Java Time"

    Instead of using `delay` to wait for a specific duration, we can also use [`delayUntil`](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/compat/compat-java-time/opensavvy.prepared.compat.java.time/delay-until.html) to wait until a specific instant.

    ```kotlin
    test("Observe what happens when the year changes") {
        time.set(Instant.parse("2024-12-31T05:00:00Z"))

        startAlgoithm(time.clockJava)
        time.delayUntil(Instant.parse("2025-01-01T01:00:00Z"))
        endAlgorithm()

        // …check the final state…
    }
    ```
