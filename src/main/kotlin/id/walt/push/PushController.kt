package id.walt.push

import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

object PushController {

    fun Application.push() {
        routing {
            route("r/push", {
                tags = listOf("Notifications / Push controlling")
            }) {
                post("subscription", {
                    summary = "Subscribe to push notifications [Experimental: Push notification system]"
                    request {
                        body<Subscription>()
                    }
                }) {
                    val subscription = call.receive<Subscription>()
                    PushManager.registerSubscription(subscription)
                    call.respond(HttpStatusCode.OK)
                }

                get("shownotif", {
                    summary = "Experimental: Push notification system"
                    // TODO
                }) {
                    PushManager.sendIssuanceNotification("abc", "http://issuer.example", listOf("VerifiableId"), "")
                }
            }
        }
    }
}
