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

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import opensavvy.prepared.runner.testballoon.preparedSuite

val server by preparedServer {
	routing {
		get("/ping") {
			call.respondText("Pong")
		}
	}
}

val basicClient by server.preparedClient()

val failFastClient by server.preparedClient {
	expectSuccess = true
}

val ServerTest by preparedSuite {
	test("The client can execute a request to the server") {
		basicClient().get("/ping").body<String>() shouldBe "Pong"
	}

	test("The client fails as expected when the route doesn't exist") {
		basicClient().get("/does-not-exist").status shouldBe HttpStatusCode.NotFound
	}

	test("The client respects its configuration") {
		shouldThrow<ClientRequestException> {
			failFastClient().get("/does-not-exist").body<String>()
		}
	}
}
