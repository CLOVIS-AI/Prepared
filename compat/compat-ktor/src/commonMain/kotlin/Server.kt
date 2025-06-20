/*
 * Copyright (c) 2024-2025, OpenSavvy and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
		serverConfig {
			parentCoroutineContext = this@prepared.environment.coroutineScope.coroutineContext
		}

		configuration(this@prepared)
	}

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
