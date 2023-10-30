package id.walt.web.controllers

import id.walt.service.dto.KeriCreateDbRequest
import id.walt.service.dto.KeriCreateDbResponse
import id.walt.service.dto.KeriInceptionRequest
import id.walt.service.dto.KeriInceptionResponse
import id.walt.service.keri.KeriInceptionService
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

            request {
                pathParameter<String>("name") {
                    description = "keystore name and file location of KERI keystore"
                    example = "waltid"
                }

                body<KeriCreateDbRequest> {
                    description = "22 character encryption passcode for keystore"
                }

            }
            response {
                HttpStatusCode.Created to {
                    body<String> {
                        description = "The salt for generating keys and the list of designated witnesses"
                        example("application/json", KeriCreateDbResponse(salt = "0123456789abcdefghijk", witnesses = listOf(
                            "http://127.0.0.1:5642/oobi/BBilc4-L3tFUnfM_wJr4S4OJanAv_VmF_dJNN6vkf2Ha",
                            "http://127.0.0.1:5643/oobi/BLskRTInXnMxWaGqcpSyMgo0nYbalW99cGZESrz3zapM",
                            "http://127.0.0.1:5644/oobi/BIKKuvBwpmDVA4Ds-EpL5bt9OqPzWPja2LigFYZN2YfX"
                        ))) {
                            summary = "Example of a CreateDatabaseRequest"
                        }
                    }
                }
            }
        }) {
            val name = call.parameters["name"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val dto = call.receive<KeriCreateDbRequest>()
            val response = KeriInitService().createKeystoreDatabase(name, dto.passcode)


            call.respond(HttpStatusCode.Created, response)


        }

        post("incept/{name}", {
            summary = "Create an inception event to initiate a controller"

            request {
                pathParameter<String>("name") {
                    description = "keystore name and file location of KERI keystore"
                    example = "waltid"
                }

                body<KeriInceptionRequest> {
                    description = "Required data for the inception of a controller"
                    example("application/json", KeriInceptionRequest(alias = "waltid-alias", passcode = "0123456789abcdefghijk"))
                }

            }
            response {
                HttpStatusCode.Created to {
                    body<String> {
                        description = "The identifiers of the controller. An AID, DID and public keys"
                        example("application/json", KeriInceptionResponse(aid = "EPjNBI4spoZ3lU5OCtNO4QgJmhIw7P1T-JOtwit36do4", did = "did:keri:EPjNBI4spoZ3lU5OCtNO4QgJmhIw7P1T-JOtwit36do4", publicKeys = listOf(
                            "DOERpUwwYCSyWH8e8yLJDKZpvEly_oJ8QJPw2wH3qRVQ"
                        ))) {
                            summary = "Example of creating an inception event (First event in a Key Event Log (KEL))"
                        }
                    }
                }
            }
        }) {
            val name = call.parameters["name"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val dto = call.receive<KeriInceptionRequest>()
            val response = KeriInceptionService().inceptController(name, dto.alias, dto.passcode)


            call.respond(HttpStatusCode.Created, response)


        }

    }
}