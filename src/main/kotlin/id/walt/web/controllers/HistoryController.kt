package id.walt.web.controllers

import id.walt.web.getWalletService
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.server.application.*
import io.ktor.server.response.*

fun Application.history() = walletRoute {
    route("history", {
        tags = listOf("History")
    }) {
        get({
            summary = "Show operation history"
        }) {
            val wallet = getWalletService()

            context.respond(wallet.getHistory())
        }
    }
}
