# Asynchronous operations

A test may need to execute other tasks concurrently with itself. 
There are two categories of concurrent tasks:

- **Foreground tasks** are considered to be part of the test: the test will fail if any of them fail,
- **Background tasks** are considered to be part of the infrastructure: the test will end as soon as all foreground tasks are finished, even if background tasks are still running.

**Identifying which category a task is a part of is crucial for the correct execution of a test.**
For example, a test will fail if some foreground tasks are still running at the end of the test timeout, whereas background tasks will be killed at the end of the timeout without impacting the test result.

!!! danger
    Depending on the test runner, the test may or may not be able to execute children tasks in parallel.

    In the case of single-threaded test runners, calling `delay` or `yield` in your code may be necessary to give a chance to tasks to run.

## Launching foreground tasks

To start a task in the foreground, use [`launch`](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/suite/opensavvy.prepared.suite/launch.html) within a test:
```kotlin
test("This is a test that starts a foreground task") {
	launch {
		delay(100)
		println("In a foreground task!")
	}
	
	println("In the test body")
}
```

The launched coroutine has the following properties:

- The test will wait for it to finish running before terminating,
- If the coroutine fails with an exception, the test will be marked as failed with that exception,
- If the test timeout is reached before the coroutine finishes, the test fails and the coroutine is dumped.

!!! tip
    Use foreground tasks to model asynchronous operations triggered by the system-under-test.

    This way, you will be warned by the test if the system-under-test doesn't clean its resources properly (e.g. if it leaks coroutines). 

## Launching background tasks

To start a task in the background, use [`launchInBackground`](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/suite/opensavvy.prepared.suite/launch-in-background.html) within a test:
```kotlin
test("This is a test that starts a background task") {
	launchInBackground {
		while(true) {
			delay(100)
			println("In a background task!")
		}
	}
	
    delay(1000)
	println("In the test body")
}
```

In this example, we start a background coroutine that performs an action every 100 milliseconds forever.
The test waits for 1 second before ending. The background task executes 10 times, then is killed when the test finishes.

The launched coroutine has the following properties:

- The coroutine will be cancelled when the test and all foreground coroutines, have finished executing,
- If the coroutine fails with an exception, it is reported but doesn't fail the test,
- If the test timeout is reached, the coroutine is dumped.

!!! tip
    Use background tasks to model operations that would outlive the system-under-test in production. For example: 

    - a keep-alive ping sent to a database,
    - a cache that has a cleanup task every few seconds,
    - to communicate the virtual time to an external service…

## Controlling the execution of external services

Sometimes, a service used inside a test must itself be able to start asynchronous operations.
When written properly, this service accepts a `CoroutineScope` on creation.

To allow the service to create foreground or background tasks, pass it either [`foregroundScope`](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/suite/opensavvy.prepared.suite/foreground-scope.html) or [`backgroundScope`](https://opensavvy.gitlab.io/groundwork/prepared/api-docs/suite/opensavvy.prepared.suite/background-scope.html):

```kotlin
val inMemoryCache by prepared { // (1)!
    // Let's declare a cache that is used by our system-under-test,
    // but isn't what we are trying to test. 
    Cache()
        .inMemory()
        .expireAfter(2.minutes, backgroundScope)
}

val systemUnderTest by prepared {
    // We are trying to test this system, and we want to ensure
    // it doesn't leak coroutines.
    SystemUnderTest(foregroundScope, inMemoryCache())
}

test("Create test users") {
    systemUnderTest().createUsers()
}
```

 1. This test uses Prepared values, which allow to reuse initialization logic between multiple test cases.
    Prepared values run as part of the test, and can thus use foreground and background tasks.
    [Learn more](prepared-values.md).

The behavior of coroutines started in each scope is identical to the equivalent `launch` variant described in the previous sections.
