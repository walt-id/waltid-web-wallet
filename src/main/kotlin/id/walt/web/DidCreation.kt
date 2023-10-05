package id.walt.web

import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.server.response.*
import io.ktor.server.routing.*

object DidCreation {

    fun Route.didCreate() {
        post("key", {
            summary = "Create a did:key"
        }) {
            val alias = context.request.queryParameters["alias"] ?: ""
            val useJwkJcsPub = context.request.queryParameters["useJwkJcsPub"]?.toBoolean() ?: false
            context.respond(
                getWalletService().createDidWithParameters(
                    "key", mapOf(
                        "useJwkJcsPub" to useJwkJcsPub
                    ),alias
                )
            )
        }

        post("jwk", {
            summary = "Create a did:jwk"
        }) {
            val alias = context.request.queryParameters["alias"] ?: ""
            context.respond(getWalletService().createDid("jwk", alias=alias))
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

            val alias = context.request.queryParameters["alias"] ?: ""

            context.respond(
                getWalletService().createDidWithParameters(
                    "web", mapOf(
                        "domain" to domain,
                        "path" to path
                    ),alias
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
            var alias = context.request.queryParameters["alias"]
            if (alias == null){
                alias =""
            }
            context.respond(
                getWalletService().createDidWithParameters(
                    "ebsi", mapOf(
                        "bearerToken" to bearerToken,
                        "version" to version
                    ),alias
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
            val alias = context.request.queryParameters["alias"] ?: ""

            context.respond(
                getWalletService().createDidWithParameters(
                    "cheqd", mapOf(
                        "network" to network
                    ),alias
                )
            )
        }

        post("iota", {
            summary = "Create a did:iota"
        }) {
            val alias = context.request.queryParameters["alias"] ?: ""

            context.respond(getWalletService().createDid("iota", alias = alias))
        }
    }

}
