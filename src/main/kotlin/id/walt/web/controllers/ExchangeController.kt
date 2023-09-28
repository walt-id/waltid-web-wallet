package id.walt.web.controllers

import id.walt.db.models.WalletOperationHistory
import id.walt.web.getWalletService
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

fun Application.exchange() = walletRoute {
    route("exchange", {
        tags = listOf("Credential exchange")
    }) {
        post("useOfferRequest", {
            request {
                queryParameter<String>("did")
                body<String> { description = "offer" }
            }
        }) {
            val wallet = getWalletService()

            val did = call.request.queryParameters["did"]
                ?: wallet.listDids().firstOrNull()
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
            request {
                queryParameter<String>("did")
                body<String> { description = "request" }
            }
        }) {
            val wallet = getWalletService()

            val did = call.request.queryParameters["did"]
                ?: wallet.listDids().firstOrNull()
                ?: throw IllegalArgumentException("No DID to use supplied")

            val request = call.receiveText()

            val redirect = wallet.usePresentationRequest(request, did)
            wallet.addOperationHistory(
                WalletOperationHistory.new(
                    wallet, "usePresentationRequest",
                    mapOf("did" to did, "request" to request, "redirect" to redirect)
                )
            )

            context.respond(HttpStatusCode.OK, mapOf("redirectUri" to redirect))
        }
        post("resolvePresentationRequest", {
            request {
                body<String> { description = "request" }
            }
        }) {
            val wallet = getWalletService()
            val request = call.receiveText()
            val parsedRequest = wallet.resolvePresentationRequest(request)
            context.respond(parsedRequest)
        }
    }
}
