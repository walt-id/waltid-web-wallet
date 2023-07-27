package id.walt.web.controllers

import id.walt.service.dto.ConnectedWalletDataTransferObject
import id.walt.service.dto.WalletDataTransferObject
import id.walt.web.getWalletService
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.json.Json
import java.util.*

fun Application.account() = walletRoute {
    route("accounts", {
        tags = listOf("Web3 wallet accounts")
    }) {
        get({
            response {
                HttpStatusCode.OK to {
                    body<List<ConnectedWalletDataTransferObject>> {
                        description = "List of connected wallets"
                    }
                }
            }
        }) {
            val wallet = getWalletService()
            context.respond<List<ConnectedWalletDataTransferObject>>(wallet.getConnectedWallets())
        }
        post("connect", {
            summary = "Connect a web3 wallet"
            request {
                body<WalletDataTransferObject> { description = "Wallet address and ecosystem" }
            }
            response {
                HttpStatusCode.OK to {
                    body<ConnectedWalletDataTransferObject> {
                        description = "TODO"
                    }
                }
            }
        }) {
            val wallet = getWalletService()
            val data = Json.decodeFromString<WalletDataTransferObject>(call.receive())
            context.respond(wallet.connectWallet(data))
        }
        post("disconnect", {
            summary = "Connect a web3 wallet"
            request {
                body<String> { description = "Wallet id" }
            }
            response {
                HttpStatusCode.OK
            }
        }) {
            val wallet = getWalletService()
            val walletId = UUID.fromString(call.receiveText())
            context.respond(wallet.disconnectWallet(walletId))
        }
    }
}
