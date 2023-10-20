package id.walt.web.controllers

import id.walt.service.issuers.IssuersService
import id.walt.usecases.issuers.CredentialDataTransferObject
import id.walt.usecases.issuers.IssuerCredentialsDataTransferObject
import id.walt.usecases.issuers.IssuerDataTransferObject
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlin.random.Random

fun Application.issuers() = walletRoute {
    route("issuers", {
        tags = listOf("Issuers")
    }) {
        get({
            summary = "List the configured issuers"
            response {
                HttpStatusCode.OK to {
                    description = "Array of issuer objects"
                    body<List<IssuerDataTransferObject>>()
                }
            }
        }) {
            context.respond(mockIssuersList())
        }
        route("{issuer}", {
            request {
                pathParameter<String>("issuer") {
                    description = "The issuer name"
                    example = "walt.id"
                }
            }
        }) {
            get({
                summary = "Fetch issuer data"

                response {
                    HttpStatusCode.OK to {
                        description = "Issuer data object"
                        body<IssuerDataTransferObject>()
                    }
                }
            }) {
                context.respond(mockIssuersList()[Random.nextInt(mockIssuersList().size)])
            }
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
                        body<List<CredentialDataTransferObject>>()
                    }
                    HttpStatusCode.InternalServerError to {
                        description = "Error message"
                        body<String>()
                    }
                }
            }) {
                val issuer = mockIssuersList()[Random.nextInt(mockIssuersList().size)]
                runCatching {
                    IssuersService.fetchCredentials(issuer.configurationEndpoint)
                }.onSuccess {
                    context.respond(IssuerCredentialsDataTransferObject(
                        issuer = issuer,
                        credentials = it
                    ))
                }.onFailure {
                    context.respondText(it.localizedMessage, ContentType.Text.Plain, HttpStatusCode.InternalServerError)
                }
            }
        }
    }
}

internal fun mockIssuersList() = listOf(
    IssuerDataTransferObject(
        name = "walt.id",
        description = "walt.id issuer portal",
        uiEndpoint = "https://portal.walt.id/credentials?ids=",
        configurationEndpoint = "https://issuer.portal.walt.id/.well-known/openid-credential-issuer",
    ),
    IssuerDataTransferObject(
        name = "walt-test#cloud",
        description = "walt-test.cloud issuer portal",
        uiEndpoint = "https://portal.walt.id/credentials?ids=",
        configurationEndpoint = "https://issuer.portal.walt.id/.well-known/openid-credential-issuer",
    ),
)