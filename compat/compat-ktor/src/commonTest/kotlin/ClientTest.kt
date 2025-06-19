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
