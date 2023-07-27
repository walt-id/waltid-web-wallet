package id.walt.web.controllers

import id.walt.web.getWalletService
import io.github.smiley4.ktorswaggerui.dsl.delete
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

fun Application.keys() = walletRoute {
    route("keys", {
        tags = listOf("Keys")
    }) {
        get({
            summary = "List Keys"
            response {
                HttpStatusCode.OK to {
                    description = "Array of (key) strings"
                    body<List<String>>()
                }
            }
        }) {
            context.respond(getWalletService().listKeys())
        }

        post("import", {
            summary = "Import an existing key"
            request {
                body<String> { description = "Key in JWK or PEM format" }
            }
        }) {
            val body = call.receiveText()
            getWalletService().importKey(body)

            context.respond(HttpStatusCode.OK)
        }

        get("load/{keyId}", {
            summary = "Load a specific key"

            request {
                pathParameter<String>("keyId") {
                    description = "the key id (or alias)"
                    example = "bc6fa6b0593648238c4616800bed7746"
                }
                queryParameter<String>("format") {
                    description = "Select format to export the key, e.g. 'JWK' / 'PEM'. JWK by default."
                    example = "JWK"
                    required = false
                }
                queryParameter<Boolean>("loadPrivateKey") {
                    description =
                        "Select if the secret private key should be loaded - take special care in this case! False by default."
                    example = false
                    required = false
                }
            }
        }) {
            val keyId = context.parameters["keyId"] ?: throw IllegalArgumentException("No key id provided.")

            val format = context.request.queryParameters["format"] ?: "JWK"
            val loadPrivateKey = context.request.queryParameters["loadPrivateKey"].toBoolean()

            context.respond(getWalletService().exportKey(keyId, format, loadPrivateKey))
        }

        delete("{keyId}", {
            summary = "Delete a specific key"
            request {
                pathParameter<String>("keyId") {
                    description = "the key id (or alias)"
                    example = "bc6fa6b0593648238c4616800bed7746"
                }
            }
        }) {
            val keyId = context.parameters["keyId"] ?: throw IllegalArgumentException("No key id provided.")

            getWalletService().deleteKey(keyId)

            context.respond(HttpStatusCode.Accepted)
        }
    }
}
