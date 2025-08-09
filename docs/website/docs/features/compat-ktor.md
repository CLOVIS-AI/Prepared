# Ktor

[Ktor](https://ktor.io/) is a popular, coroutine-first HTTP library developed by JetBrains. Ktor allows developing client and server applications, allowing to share code between all platforms and roles. 

Prepared provides helpers to easily test clients and servers.

!!! info "Configuration"
    Add a dependency on `dev.opensavvy.prepared:compat-ktor` to use the features on this page.

    See the [reference](https://prepared.opensavvy.dev/api-docs/compat/compat-ktor/index.html).

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

We want to write tests to ensure:

- If the correct endpoint is requested, the response is correct,
- If an incorrect endpoint is requested, the server returns a 404 error.

We use the  to instantiate a server:

```kotlin
val server by preparedServer { //(1)!
	ping() //(2)!
}

val client by server.preparedClient() //(3)!
```

1.  The [`preparedServer`](https://prepared.opensavvy.dev/api-docs/compat/compat-ktor/opensavvy.prepared.compat.ktor/prepared-server.html) function declares a Ktor server as a [prepared value](prepared-values.md).
2.  Load the server module we want to test. <br/>If you use the `application.conf` file, modules are loaded automatically.
3.  The [`preparedClient`](https://prepared.opensavvy.dev/api-docs/compat/compat-ktor/opensavvy.prepared.compat.ktor/prepared-client.html) function declares a Ktor client based on a server, as a [prepared value](prepared-values.md).

```kotlin
fun SuiteDsl.pingTest() = suite("Testing the ping server") {
	test("The /ping route returns Pong") {
		client().get("/ping").body<String>() shouldBe "Pong"
	}

	test("A route that doesn't exist returns a 404") {
		client().get("/does-not-exist").status shouldBe HttpStatusCode.NotFound
	}
}
```

Because the server and client are declared as [prepared values](prepared-values.md), referring to the client automatically instantiates the server. The server is automatically stopped at the end of the test.

!!! info
    This example uses the [Kotest assertion library](../tutorials/index.md#assertion-libraries).

To learn more about configuring the test server, see [the official documentation](https://ktor.io/docs/testing.html).

## Testing a client

If we have a client we want to test, we can use the same approach to instead create a fake server.
Let's assume we have the following client:

```kotlin
/**
 * A simple method that checks if a server is online.
 */
suspend fun HttpClient.isOnline(): Boolean =
	get("/ping").status.isSuccess()
```

We want to write tests to ensure that:

- If the server does have this endpoint, the function returns `true`,
- If the server does not have this endpoint, the function returns `false`,
- If the server does have this endpoint, but it fails, returns `false`.

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

val brokenServer by preparedServer {
	routing {
		get("/ping") {
			error("This server is broken")
		}
	}
}

test("A server with a /ping route that responds is online") {
	pingableServer().client.isOnline() shouldBe true
}

test("A server without a /ping route is offline") {
	// Tests that the client detects the server is offline, 
	// without throwing an exception or breaking in any other way
	incompleteServer().client.isOnline() shouldBe false
}

test("A server with a broken /ping route is offline") {
	// Tests that the client detects the server is offline, 
	// without throwing an exception or breaking in any other way
	brokenServer().client.isOnline() shouldBe false
}
```

!!! info
    This example uses the [Kotest assertion library](../tutorials/index.md#assertion-libraries).

## Testing both

When writing multiplatform fullstack applications, we can share our entire DTO logic between client and server. Using the techniques outlined in this article, we can easily test that they are both capable of communicating, by configuring our real server in the `preparedServer` function, and our real client in the `preparedClient` function.

This way, we ensure that our API always evolves in lockstep: our client and server are always able to communicate with each other, because we wrote tests for it. This guarantee isn't possible with other testing technologies (like Wiremock) because they test the server and the client against stubs which have no guarantee of corresponding to the real world.
