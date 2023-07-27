package id.walt.web

import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.server.response.*
import io.ktor.server.routing.*

object DidCreation {

    fun Route.didCreate() {
        post("key", {
            summary = "Create a did:key"
        }) {
            val useJwkJcsPub = context.request.queryParameters["useJwkJcsPub"]?.toBoolean() ?: false
            context.respond(
                getWalletService().createDidWithParameters(
                    "key", mapOf(
                        "useJwkJcsPub" to useJwkJcsPub
                    )
                )
            )
        }

        post("jwk", {
            summary = "Create a did:jwk"
        }) {
            context.respond(getWalletService().createDid("jwk"))
        }

        post("web", {
            summary = "Create a did:web"
            request {
                queryParameter<String>("domain")
                queryParameter<String>("path")
            }
        }) {
            val domain = context.request.queryParameters["domain"]
            val path = context.request.queryParameters["path"]
            context.respond(
                getWalletService().createDidWithParameters(
                    "web", mapOf(
                        "domain" to domain,
                        "path" to path
                    )
                )
            )
        }

        post("ebsi", {
            summary = "Create a did:ebsi"
            request {
                queryParameter<Int>("version") { description = "Version 2 (NaturalPerson) or 1 (LegalEntity)" }
                queryParameter<String>("bearerToken") { description = "Required for v1 (LegalEntity)" }
            }
        }) {
            val version = context.request.queryParameters["version"]?.toInt()
            val bearerToken = context.request.queryParameters["bearerToken"]
            context.respond(
                getWalletService().createDidWithParameters(
                    "ebsi", mapOf(
                        "bearerToken" to bearerToken,
                        "version" to version
                    )
                )
            )
        }

        post("cheqd", {
            summary = "Create a did:cheqd"
            request {
                queryParameter<String>("network") { description = "testnet or mainnet" }
            }
        }) {
            val network = context.request.queryParameters["network"]
            context.respond(
                getWalletService().createDidWithParameters(
                    "cheqd", mapOf(
                        "network" to network
                    )
                )
            )
        }

        post("iota", {
            summary = "Create a did:iota"
        }) {
            context.respond(getWalletService().createDid("iota"))
        }
    }

}
