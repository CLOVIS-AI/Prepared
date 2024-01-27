package opensavvy.prepared.compat.ktor

import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import opensavvy.prepared.runner.kotest.PreparedSpec

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

class ClientTest : PreparedSpec({
	test("The pingable server responds to pings") {
		pingableServer().client.get("/ping").status shouldBe HttpStatusCode.OK
	}

	test("The client fails graciously when the server doesn't respond") {
		incompleteServer().client.get("/ping").status shouldBe HttpStatusCode.NotFound
	}
})
