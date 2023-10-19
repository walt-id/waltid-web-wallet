package id.walt.web.controllers

import id.walt.usecases.issuers.IssuerCredentialDataTransferObject
import id.walt.usecases.issuers.IssuerDataTransferObject
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

fun Application.issuers() = walletRoute {
    route("issuers", {
        tags = listOf("Issuers")
    }) {
        get({
            summary = "List issuers"
            response {
                HttpStatusCode.OK to {
                    description = "Array of issuer objects"
                    body<List<IssuerDataTransferObject>>()
                }
            }
        }) {
            context.respond(mockIssuersList())
        }
        route("{issuer}/credentials", {
            request {
                pathParameter<String>("issuer") {
                    description = "The issuer name"
                    example = "walt.id"
                }
            }
        }) {
            get({
                summary = "Show supported credentials for the given issuer"

                response {
                    HttpStatusCode.OK to {
                        description = "Array of issuer credential objects"
                        body<List<IssuerCredentialDataTransferObject>>()
                    }
                }
            }) {
                context.respond(mockCredentialsList())
            }
        }
    }
}

internal fun mockIssuersList() = listOf(
    IssuerDataTransferObject(
        name = "walt.id",
        description = "walt.id issuer portal",
    ),
    IssuerDataTransferObject(
        name = "walt-test#cloud",
        description = "walt-test.cloud issuer portal",
    ),
)

internal fun mockCredentialsList() = listOf(
    IssuerCredentialDataTransferObject(
        type = "PermanentResidentCard",
        uiEndpoint = "https://portal.walt.id",
        callbackUrl = "https://wallet.portal.walt.id",
    ),
    IssuerCredentialDataTransferObject(
        type = "VerifiableAttestation",
        uiEndpoint = "https://portal.walt.id",
        callbackUrl = "https://wallet.portal.walt.id",
    ),
    IssuerCredentialDataTransferObject(
        type = "OpenBadgeCredential",
        uiEndpoint = "https://portal.walt.id",
        callbackUrl = "https://wallet.portal.walt.id",
    ),
)