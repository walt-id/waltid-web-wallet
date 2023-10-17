package id.walt.web

import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.JsonPrimitive

object DidCreation {

    fun Route.didCreate() {
        post("key", {
            summary = "Create a did:key"
            request {
                queryParameter<Boolean?>("useJwkJcsPub") {
                    description = "Optionally set JWK JCS Pub format (for e.g. EBSI)"
                    required = false
                }
            }
        }) {
            getWalletService().createDid(
                "key", mapOf(
                    "useJwkJcsPub" to JsonPrimitive(
                        context.request.queryParameters["useJwkJcsPub"]?.toBoolean() ?: false
                    ), "alias" to JsonPrimitive(context.request.queryParameters["alias"])
                )
            ).let { context.respond(it) }
        }

        post("jwk", {
            summary = "Create a did:jwk"
        }) {
            getWalletService().createDid(
                "jwk", mapOf("alias" to JsonPrimitive(context.request.queryParameters["alias"]))
            ).let { context.respond(it) }
        }

        post("web", {
            summary = "Create a did:web"
            request {
                queryParameter<String>("domain") {
                    description = "Domain to use to host did:web document at"
                }
                queryParameter<String>("path") {
                    description = "Path to host the did:web document at"
                }
            }
        }) {
            getWalletService().createDid(
                "web", mapOf(
                    "domain" to JsonPrimitive(context.request.queryParameters["domain"]),
                    "path" to JsonPrimitive(context.request.queryParameters["path"]),
                    "alias" to JsonPrimitive(context.request.queryParameters["alias"])
                )
            ).let { context.respond(it) }
        }

        post("ebsi", {
            summary = "Create a did:ebsi"
            request {
                queryParameter<Int>("version") { description = "Version 2 (NaturalPerson) or 1 (LegalEntity)" }
                queryParameter<String>("bearerToken") { description = "Required for v1 (LegalEntity)" }
            }
        }) {
            getWalletService().createDid(
                "ebsi", mapOf(
                    "bearerToken" to JsonPrimitive(context.request.queryParameters["bearerToken"]),
                    "version" to JsonPrimitive(context.request.queryParameters["version"]?.toInt()),
                    "alias" to JsonPrimitive(context.request.queryParameters["alias"])
                )
            ).let { context.respond(it) }
        }

        post("cheqd", {
            summary = "Create a did:cheqd"
            request {
                queryParameter<String>("network") { description = "testnet or mainnet" }
            }
        }) {
            getWalletService().createDid(
                "cheqd", mapOf(
                    "network" to JsonPrimitive(context.request.queryParameters["network"]),
                    "alias" to JsonPrimitive(context.request.queryParameters["alias"])
                )
            ).let { context.respond(it) }
        }

        post("iota", {
            summary = "Create a did:iota"
        }) {
            getWalletService().createDid(
                "iota", mapOf("alias" to JsonPrimitive(context.request.queryParameters["alias"]))
            ).let { context.respond(it) }
        }
    }

}
