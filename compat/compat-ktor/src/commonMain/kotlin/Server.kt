package opensavvy.prepared.compat.ktor

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.server.testing.*
import opensavvy.prepared.suite.Prepared
import opensavvy.prepared.suite.TestDsl
import opensavvy.prepared.suite.cleanUp
import opensavvy.prepared.suite.prepared

/**
 * Instantiates a Ktor test server.
 *
 * The server can be configured to add plugins, routes, external services to be mocked, etc.
 * For example:
 * ```kotlin
 * val server by preparedServer {
 *     install(ContentNegotiation) {
 *        json()
 *     }
 *
 *     externalServices {
 *         hosts("https://www.googleapis.com") {
 *             // …
 *         }
 *     }
 *
 *     routing {
 *         get("/ping") {
 *             call.respondText("Pong")
 *         }
 *     }
 * }
 * ```
 * The configuration lambda is identical to the one passed to the [testApplication] Ktor function. To learn more, visit [the Ktor documentation](https://ktor.io/docs/testing.html).
 *
 * @see preparedClient Instantiate a client from this server.
 */
fun preparedServer(configuration: TestApplicationBuilder.(test: TestDsl) -> Unit = {}) = prepared {
	val application = TestApplication {
		environment {
			parentCoroutineContext = environment.coroutineScope.coroutineContext
		}

		configuration(this@prepared)
	}

	application.start()

	cleanUp("Stopping the test server") {
		application.stop()
	}

	application
}

/**
 * Instantiates a Ktor test client from a test server.
 *
 * This overload allows configuring the created client. It is sugar for [TestApplication.createClient].
 */
fun Prepared<TestApplication>.preparedClient(configuration: HttpClientConfig<out HttpClientEngineConfig>.(test: TestDsl) -> Unit) = prepared {
	val server = this@preparedClient()

	val client = server.createClient {
		configuration(this@prepared)
	}

	client
}

/**
 * Instantiates a Ktor test client from a test server.
 *
 * This function is sugar for [TestApplication.client].
 */
fun Prepared<TestApplication>.preparedClient() = prepared {
	this@preparedClient().client
}
