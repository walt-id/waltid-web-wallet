package id.walt.web.controllers

import id.walt.web.DidCreation.didCreate
import id.walt.web.getWalletService
import io.github.smiley4.ktorswaggerui.dsl.delete
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.json.JsonObject

fun Application.dids() = walletRoute {
    route("dids", {
        tags = listOf("DIDs")
    }) {
        get({
            summary = "List DIDs"
            response {
                HttpStatusCode.OK to {
                    description = "Array of (DID) strings"
                    body<List<String>>()
                }
            }
        }) {
            context.respond(getWalletService().listDids())
        }

        get("{did}", {
            summary = "Show a specific DID"
            request {
                pathParameter<String>("did") {
                    description = "the did string"
                    example = "did:web:walt.id"
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "The DID document"
                    body<JsonObject>()
                }
            }
        }) {
            context.respond(
                getWalletService().loadDid(
                    context.parameters["did"] ?: throw IllegalArgumentException("No DID supplied")
                )
            )
        }

        delete("{did}", {
            summary = "Delete a specific DID"
            request {
                pathParameter<String>("did") {
                    description = "the did string"
                    example = "did:web:walt.id"
                }
            }
        }) {
            getWalletService().deleteDid(
                context.parameters["did"] ?: throw IllegalArgumentException("No DID supplied")
            )
        }

        route("create", {
            request {
                queryParameter<String>("keyId")
            }
        }) {
            didCreate()
        }
    }
}
