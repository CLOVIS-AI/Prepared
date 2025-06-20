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

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import opensavvy.prepared.runner.testballoon.preparedSuite

val pingableServer by preparedServer {
	routing {
		get("/ping") {
			call.respondText("Pong")
		}
	}
}

val incompleteServer by preparedServer {
	// No '/ping' route
}

val ClientTest by preparedSuite {
	test("The pingable server responds to pings") {
		check(pingableServer().client.get("/ping").status == HttpStatusCode.OK)
	}

	test("The client fails graciously when the server doesn't respond") {
		check(incompleteServer().client.get("/ping").status == HttpStatusCode.NotFound)
	}
}
