# Module Compatibility with Ktor

Test your Ktor clients and servers using an in-memory implementation.

<a href="https://search.maven.org/search?q=dev.opensavvy.prepared.compat-ktor"><img src="https://img.shields.io/maven-central/v/dev.opensavvy.prepared/compat-ktor.svg?label=Maven%20Central"></a>

This module is built upon the [Ktor Test Host](https://ktor.io/docs/testing.html).
It uses an in-memory implementation of the server and client engines, allowing faster tests.
Since both the client and server run in the same process, these tests are easier to debug.

## Testing a server

This module can be used to test that your server is functioning correctly.
Let's imagine that you have the following server module:

```kotlin
fun Application.ping() {
	install(CallLogging) { /* … */ }

	routing {
		get("/ping") {
			call.respondText("Pong")
		}
	}
}
```

We want to write tests that ensures that:

- If the correct endpoint is requested, the response is correct,
- If an incorrect endpoint is requested, the server returns a 404 error.

We use the [preparedServer][opensavvy.prepared.compat.ktor.preparedServer] function to instantiate a server:

```kotlin
// Declare the server
val server by preparedServer {
	// Configure the server with our application module…
	ping()
	// …if you use the application.conf file, 
	// the module is loaded automatically.
}

// Declare a client (in this case, we need no further configuration)
val client by server.preparedClient()
```

In this example, we use the Kotest assertions:

```kotlin
fun SuiteDsl.pingTest() = suite("Testing the ping server") {
	test("The /ping route returns Pong") {
		// Referring to the client automatically instantiates the server
		client().get("/ping").body<String>() shouldBe "Pong"
	}

	test("A route that doesn't exist returns a 404") {
		client().get("/does-not-exist").status shouldBe HttpStatusCode.NotFound
	}
}
```

To learn more about configuring the test server, see [the official documentation](https://ktor.io/docs/testing.html). In particular, you will learn how to

## Testing a client

If we have a client we want to test, we can use the same approach to instead create a fake server.
Let's assume we have the following client:

```kotlin
import kotlin.time.TimeSource
import kotlin.time.measureTime

/**
 * A simple method that checks if a server is online.
 */
suspend fun HttpClient.isOnline(): Boolean {
	return get("/ping").status.isSuccess()
}
```

We want to write tests to ensure that:

- If the server does respond, the measurement is correct,
- If the server doesn't have this route, the function does return `null`.

In this example, we use the Kotest assertions:

```kotlin
val pingableServer by preparedServer {
	routing {
		get("/ping") {
			call.respondText("Pong")
		}
	}
}

val unpingableServer by preparedServer {
	// No '/ping' route
}

test("The client queries the ping route correctly") {
	pingableServer().client.isOnline() shouldBe true
}

test("The client fails graciously when the server doesn't respond") {
	// Tests that the client detects the server is offline, without throwing an exception or breaking in any other way
	incompleteServer().client.isOnline() shouldBe false
}
```

Of course, both techniques can be combined to test both the client and server together.
