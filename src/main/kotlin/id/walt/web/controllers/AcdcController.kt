package id.walt.web.controllers

import id.walt.service.dto.*
import id.walt.service.keri.AcdcManagementService
import id.walt.service.keri.AcdcSaidifyService
import id.walt.service.keri.IpexService
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.delete
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

fun Application.acdc() = walletRoute {
    route("acdc", {
        tags = listOf("Authentic Chain Data Containers (ACDC) Management")
    }) {
        post("saidify", {
            summary = "Populate file with Self-Addressing IDentifier (SAID)"

            request {
                body<AcdcSaidifyRequest> {
                    description = "File name, type the full file location and name with extension"
                }
            }

            response {
                HttpStatusCode.Created to {
                    body<AcdcSaidifyResponse> {
                        description = "The SAID and a fully saidified file"
                        example("application/json", AcdcSaidifyResponse(said = "EFLhmGcItk9WPXWvdV_X9XtGwfg4IJR4TLUSTHXMgWhi", file= "\"{\\\"d\\\": " +
                                "\\\"EFLhmGcItk9WPXWvdV_X9XtGwfg4IJR4TLUSTHXMgWhi\\\", \\\"privacyDisclaimer\\\": {\\\"l\\\": \\\"It is the sole responsibility " +
                                "of Holders of a CustomChildCredential to present that credential in a privacy-preserving manner using the mechanisms " +
                                "provided in the Issuance and Presentation Exchange (IPEX) protocol specification and the Authentic Chained Data " +
                                "Container (ACDC) specification. https://github.com/WebOfTrust/IETF-IPEX and https://github.com/trustoverip/tswg-acdc-specification.\\\"}}\"")) {
                            summary = "Example of a Saidifying a file"
                        }
                    }
                }
            }
        }) {
            val dto = call.receive<AcdcSaidifyRequest>()
            val response = AcdcSaidifyService().saidify(dto.filename)

            if (response.said == "") {
                call.respond(HttpStatusCode.InternalServerError)
            } else {
                call.respond(HttpStatusCode.Created, response)
            }
        }

        post("ipex/apply", {
            summary = "Disclosee: Request a credential from another party by initiating an IPEX exchange"

            response {
                HttpStatusCode.OK to {
                    body<IpexSaid> {
                        example("application/json", IpexSaid(said = "EFLhmGcItk9WPXWvdV_X9XtGwfg4IJR4TLUSTHXMgWhi")) {
                        }
                    }
                }
            }
        }) {
            val response = IpexService().apply()
            call.respond(HttpStatusCode.OK, response)
        }

        post("ipex/offer", {
            summary = "Discloser: Reply to IPEX apply message or initiate an IPEX exchange with an offer for a credential with certain characteristics"

            response {
                HttpStatusCode.OK to {
                    body<IpexSaid> {
                        example("application/json", IpexSaid(said = "AIq50EHA8J634LNlpq7_b8PkL17OV5KcAae91cMp8g7h")) {
                        }
                    }
                }
            }
        }) {
            val response = IpexService().offer()
            call.respond(HttpStatusCode.OK, response)
        }

        post("ipex/agree", {
            summary = "Disclosee: Reply to IPEX offer message acknowledged willingness to accept offered credential"

            response {
                HttpStatusCode.OK to {
                    body<IpexSaid> {
                        example("application/json", IpexSaid(said = "AGjwWfC9LlDCCsSPdPomRmMZOIIeqPfHIA5V3hjyzf7D")) {
                        }
                    }
                }
            }

        }) {
            val response = IpexService().agree()
            call.respond(HttpStatusCode.OK, response)
        }

        post("ipex/grant/keystore/{keystore}/alias/{alias}", {
            summary = ""

            response {
                HttpStatusCode.OK to {
                    body<IpexSaid> {
                        example("application/json", IpexSaid(said = "AGjwWfC9LlDCCsSPdPomRmMZOIIeqPfHIA5V3hjyzf7D")) {
                        }
                    }
                }
            }

        }) {
            val keystore = call.parameters["keystore"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val alias = call.parameters["alias"] ?: return@post call.respond(HttpStatusCode.BadRequest)

            val dto = call.receive<IpexGrant>()
            val response = IpexService().grant(keystore, alias, dto.passcode, dto.credentialSaid, dto.recipient, dto.message)
            call.respond(HttpStatusCode.OK, response)
        }

        post("ipex/admit/keystore/{keystore}/alias/{alias}", {
            summary = "Accept a credential being issued or presented in response to an IPEX grant"

            request {
                pathParameter<String>("keystore") {
                    description = "keystore name and file location of KERI keystore"
                    example = "waltid"
                }

                pathParameter<String>("alias") {
                    description = "human readable alias for the identifier to whom the credential was issued"
                    example = "waltid-alias"
                }

                body<KeriInceptionRequest> {
                    description = "Required data for admitting an event"
                    example("application/json", IpexAdmit(
                        passcode = "0123456789abcdefghijk",
                        said = "EBfdlu8R27Fbx-ehrqwImnK-8Cm79sqbAQ4MmvEAYqao",
                        message = "Sent to a friend"
                    ))
                }
            }

        }) {
            val keystore = call.parameters["keystore"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val alias = call.parameters["alias"] ?: return@post call.respond(HttpStatusCode.BadRequest)

            val dto = call.receive<IpexAdmit>()
            val response = IpexService().admit(keystore, alias, dto.passcode, dto.said, dto.message)
            call.respond(HttpStatusCode.OK, response)
        }

        get("ipex/list/keystore/{keystore}/alias/{alias}", {
            summary = "List notifications related to IPEX protocol message"

            request {
                pathParameter<String>("keystore") {
                    description = "keystore name and file location of KERI keystore"
                    example = "waltid"
                }

                pathParameter<String>("alias") {
                    description = "human readable alias for the identifier to whom the credential was issued"
                    example = "waltid-alias"
                }

                body<KeriInceptionRequest> {
                    description = "Required data for listing an event"
                    example("application/json", IpexList(
                        passcode = "0123456789abcdefghijk",
                        schema = "m2rz53livjpiINfPYRIO_VWje6VfhF6CtHJcbzyyNtE",
                        type = IPEX_EVENT.GRANT,
                        poll = true,
                        said = true,
                        sent = false,
                        verbose = false
                        ))
                }
            }

            response {
                HttpStatusCode.OK to {
                    body<IpexSaid> {
                        example("application/json", IpexSaid(said = "AGjwWfC9LlDCCsSPdPomRmMZOIIeqPfHIA5V3hjyzf7D")) {
                        }
                    }
                }
            }

        }) {
            val keystore = call.parameters["keystore"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val alias = call.parameters["alias"] ?: return@get call.respond(HttpStatusCode.BadRequest)

            val dto = call.receive<IpexList>()
            val response = IpexService().list(
                keystore,
                alias,
                dto.passcode,
                dto.schema,
                dto.type,
                dto.verbose,
                dto.poll,
                dto.sent,
                dto.said
            )
            call.respond(HttpStatusCode.OK, response)
        }

        delete("ipex/admit/keystore/{keystore}/alias/{alias}", {
            summary = "Reject an IPEX apply, offer, agree or grant message"

            request {
                pathParameter<String>("keystore") {
                    description = "keystore name and file location of KERI keystore"
                    example = "waltid"
                }

                pathParameter<String>("alias") {
                    description = "human readable alias for the identifier to whom the credential was issued"
                    example = "waltid-alias"
                }

                body<KeriInceptionRequest> {
                    description = "Required data for rejecting an event"
                    example("application/json", IpexSpurn(
                        passcode = "0123456789abcdefghijk",
                        said = "EBfdlu8R27Fbx-ehrqwImnK-8Cm79sqbAQ4MmvEAYqao",
                        message = "Sent to a friend"
                    ))
                }
            }
        }) {
            val keystore = call.parameters["keystore"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            val alias = call.parameters["alias"] ?: return@delete call.respond(HttpStatusCode.BadRequest)

            val dto = call.receive<IpexSpurn>()
            IpexService().spurn(keystore, alias, dto.passcode, dto.said, dto.message)
            call.respond(HttpStatusCode.NoContent)
        }

    }

    get("list/keystore/{keystore}/alias/{alias}", {
        summary = "List credentials and check mailboxes for any newly issued credentials"

    }) {
        val keystore = call.parameters["keystore"] ?: return@get call.respond(HttpStatusCode.BadRequest)
        val alias = call.parameters["alias"] ?: return@get call.respond(HttpStatusCode.BadRequest)

        val dto = call.receive<AcdcList>()
        val response = AcdcManagementService().list(keystore,
            alias,
            dto.passcode,
            dto.verbose,
            dto.poll,
            dto.issued,
            dto.said,
            dto.schema)
        call.respond(HttpStatusCode.OK, response)
    }

}