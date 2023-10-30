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

            request {
                body<AcdcSaidifyRequest> {
                    description = "File name, type the full file location and name with extension"
                }
            }

            response {
                HttpStatusCode.Created to {
                    body<AcdcSaidifyResponse> {
                        description = "The SAID and a fully saidified file"
                        example("application/json", AcdcSaidifyResponse(said = "EFLhmGcItk9WPXWvdV_X9XtGwfg4IJR4TLUSTHXMgWhi", file= "\"{\\\"d\\\": " +
                                "\\\"EFLhmGcItk9WPXWvdV_X9XtGwfg4IJR4TLUSTHXMgWhi\\\", \\\"privacyDisclaimer\\\": {\\\"l\\\": \\\"It is the sole responsibility " +
                                "of Holders of a CustomChildCredential to present that credential in a privacy-preserving manner using the mechanisms " +
                                "provided in the Issuance and Presentation Exchange (IPEX) protocol specification and the Authentic Chained Data " +
                                "Container (ACDC) specification. https://github.com/WebOfTrust/IETF-IPEX and https://github.com/trustoverip/tswg-acdc-specification.\\\"}}\"")) {
                            summary = "Example of a Saidifying a file"
                        }
                    }
                }
            }
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