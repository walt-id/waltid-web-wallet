package id.walt.web.controllers

import id.walt.service.dto.*
import id.walt.service.keri.AcdcSaidifyService
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

fun Application.acdc() = walletRoute {
    route("acdc", {
        tags = listOf("Authentic Chain Data Containers (ACDC) Management")
    }) {


        post("saidify", {
            summary = "Populate file with Self-Addressing IDentifier (SAID)"
        }) {
            val dto = call.receive<AcdcSaidifyRequest>()
            val response = AcdcSaidifyService().saidify(dto.filename)

            if (response.said == "") {
                call.respond(HttpStatusCode.InternalServerError)
            } else {
                call.respond(HttpStatusCode.Created, response)
            }


        }

    }
}