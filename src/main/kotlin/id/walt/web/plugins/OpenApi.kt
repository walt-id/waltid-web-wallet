package id.walt.web.plugins

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.AuthKeyLocation
import io.github.smiley4.ktorswaggerui.dsl.AuthScheme
import io.github.smiley4.ktorswaggerui.dsl.AuthType
import io.ktor.server.application.*

fun Application.configureOpenApi() {
    install(SwaggerUI) {
        swagger {
            swaggerUrl = "swagger"
            forwardRoot = true
        }
        info {
            title = "walt.id wallet API"
            version = "latest"
            description = "Interact with the wallet backend"
        }
        server {
            url = "http://localhost:4545"
            description = "Development Server"
        }

        securityScheme("authenticated-session") {
            type = AuthType.API_KEY
            location = AuthKeyLocation.COOKIE
        }

        securityScheme("authenticated-bearer") {
            type = AuthType.API_KEY
            location = AuthKeyLocation.HEADER
            scheme = AuthScheme.BEARER
        }

        defaultUnauthorizedResponse {
            description = "Invalid authentication"
        }
    }
}
