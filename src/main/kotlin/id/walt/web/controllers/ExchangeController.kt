package id.walt.web.controllers

import id.walt.service.dto.WalletOperationHistory
import id.walt.web.getWalletService
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.json.JsonObject

fun Application.exchange() = walletRoute {
    route("exchange", {
        tags = listOf("Credential exchange")
    }) {
        post("useOfferRequest", {
            summary = "Claim credential(s) from an issuer"

            request {
                queryParameter<String>("did") { description = "The DID to issue the credential(s) to" }
                body<String> {
                    description = "The offer request to use"
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "Successfully claimed credentials"
                }
            }
        }) {
            val wallet = getWalletService()

            val did = call.request.queryParameters["did"]
                ?: wallet.listDids().firstOrNull()?.did
                ?: throw IllegalArgumentException("No DID to use supplied")

            val offer = call.receiveText()

            wallet.useOfferRequest(offer, did)
            wallet.addOperationHistory(
                WalletOperationHistory.new(
                    wallet, "useOfferRequest",
                    mapOf("did" to did, "offer" to offer)
                )
            )

            context.respond(HttpStatusCode.OK)
        }

        post("usePresentationRequest", {
            summary = "Present credential(s) to a Relying Party"

            request {
                queryParameter<String>("did") { description = "The DID to present the credential(s) from" }
                body<String> { description = "Presentation request" }
            }
            response {
                HttpStatusCode.OK to {
                    description = "Successfully claimed credentials"
                    body<JsonObject> {
                        description = """{"redirectUri": String}"""
                    }
                }
            }
        }) {
            val wallet = getWalletService()

            val did = call.request.queryParameters["did"]
                ?: wallet.listDids().firstOrNull()?.did
                ?: throw IllegalArgumentException("No DID to use supplied")

            val request = call.receiveText()

            val result = wallet.usePresentationRequest(request, did)


            if (result.isSuccess) {
                wallet.addOperationHistory(
                    WalletOperationHistory.new(
                        wallet, "usePresentationRequest",
                        mapOf("did" to did, "request" to request, "success" to "true", "redirect" to result.getOrThrow()) // change string true to bool
                    )
                )

                context.respond(HttpStatusCode.OK, mapOf("redirectUri" to result.getOrThrow()))
            } else {
                wallet.addOperationHistory(
                    WalletOperationHistory.new(
                        wallet, "usePresentationRequest",
                        mapOf("did" to did, "request" to request, "success" to "false", "redirect" to result.getOrThrow()) // change string true to bool
                    )
                )

                context.respond(HttpStatusCode.BadRequest, mapOf("redirectUri" to result.exceptionOrNull()!!.message))
            }
        }
        post("resolvePresentationRequest", {
            summary = "Return resolved / parsed presentation request"

            request {
                body<String> { description = "PresentationRequest to resolve/parse" }
            }
            response {
                HttpStatusCode.OK to {
                    body<String>()
                }
            }
        }) {
            val wallet = getWalletService()
            val request = call.receiveText()
            val parsedRequest = wallet.resolvePresentationRequest(request)
            context.respond(parsedRequest)
        }
    }
}
