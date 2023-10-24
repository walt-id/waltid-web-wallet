package id.walt.web.controllers

import id.walt.service.dto.KeriCreateDbRequest
import id.walt.service.keri.KeriInitService
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

fun Application.keri() = walletRoute {
    route("keri", {
        tags = listOf("Key Event Receipt Infrastructure (KERI)")
    }) {

        post("db/{name}", {
            summary = "Create an encrypted database and keystore for an entity"
        }) {
            val name = call.parameters["name"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val dto = call.receive<KeriCreateDbRequest>()
            val response = KeriInitService().createKeystoreDatabase(name, dto.passcode)


            call.respond(HttpStatusCode.Created, response)


        }

    }
}