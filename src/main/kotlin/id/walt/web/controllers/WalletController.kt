package id.walt.web.controllers

import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.walletRoute(build: Route.() -> Unit) {
    routing {
        authenticate("authenticated-session", "authenticated-bearer") {
            route("r/wallet/{wallet}", {
                // tags = listOf("wallet")
            }) {
                build.invoke(this)
            }
        }
    }
}
