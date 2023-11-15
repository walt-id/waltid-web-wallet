package id.walt.web

//import id.walt.web.model.LoginRequestJson
import id.walt.service.WalletServiceManager
import id.walt.service.account.AccountsService
import id.walt.utils.RandomUtils
import id.walt.web.model.AccountRequest
import id.walt.web.model.EmailAccountRequest
import id.walt.web.model.LoginRequestJson
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.uuid.UUID
import kotlin.collections.set
import kotlin.time.Duration.Companion.days

private val log = KotlinLogging.logger { }

@Suppress("ArrayInDataClass")
data class ByteLoginRequest(val username: String, val password: ByteArray) {
    constructor(loginRequest: EmailAccountRequest) : this(loginRequest.email, loginRequest.password.toByteArray())

    override fun toString() = "[LOGIN REQUEST FOR: $username]"
}

fun generateToken() = RandomUtils.randomBase64UrlString(256)

data class LoginTokenSession(val token: String) : Principal

fun Application.configureSecurity() {

    install(Sessions) {
        val encryptionKey = "uv4phoozeefoom7l".toByteArray()
        val signKey = "faungeenah5aewiL".toByteArray()

        cookie<LoginTokenSession>("login") {
            //cookie.encoding = CookieEncoding.BASE64_ENCODING

            //cookie.httpOnly = true
            cookie.httpOnly = false // FIXME
            // TODO cookie.secure = true
            cookie.maxAge = 1.days
            cookie.extensions["SameSite"] = "Strict"
            transform(SessionTransportTransformerEncrypt(encryptionKey, signKey))
        }
    }

    install(Authentication) {

        bearer {
            bearer("authenticated-bearer") {
                authenticate { tokenCredential ->
                    if (securityUserTokenMapping.contains(tokenCredential.token)) {
                        UserIdPrincipal(securityUserTokenMapping[tokenCredential.token].toString())
                    } else {
                        null
                    }
                }
            }
        }

        session<LoginTokenSession>("authenticated-session") {
            validate { session ->
                //println("Validating: $session, [$securityUserTokenMapping]")
                if (securityUserTokenMapping.contains(session.token)) {
                    UserIdPrincipal(securityUserTokenMapping[session.token].toString())
                } else {
                    sessions.clear("login")
                    null
                }
            }

            challenge {
                call.respond(HttpStatusCode.Unauthorized, "Login to continue.")
            }
        }
    }
}


val securityUserTokenMapping = HashMap<String, UUID>() // Token -> UUID


fun Application.auth() {
    routing {
        route("r/auth", {
            tags = listOf("Authentication")
        }) {
            post("login", {
                summary = "Login with [email + password] or [wallet address + ecosystem]"
                request {
                    body<EmailAccountRequest> {
                        example("E-mail + password", buildJsonObject {
                            put("username", JsonPrimitive("user@email.com"))
                            put("password", JsonPrimitive("password"))
                            put("type", JsonPrimitive("email"))
                        }.toString())
                        example("Wallet address + ecosystem", buildJsonObject {
                            put("address", JsonPrimitive("0xABC"))
                            put("ecosystem", JsonPrimitive("ecosystem"))
                            put("type", JsonPrimitive("address"))
                        }.toString())
                    }
                }
                response {
                    HttpStatusCode.OK to { description = "Login successful" }
                    HttpStatusCode.Unauthorized to { description = "Login failed" }
                    HttpStatusCode.BadRequest to { description = "Login failed" }
                }
            }) {
                println("Login request")
                val reqBody = LoginRequestJson.decodeFromString<AccountRequest>(call.receive())
                AccountsService.authenticate(reqBody).onSuccess {
                    securityUserTokenMapping[it.token] = it.id
                    call.sessions.set(LoginTokenSession(it.token))
                    call.response.status(HttpStatusCode.OK)
                    call.respond(
                        mapOf(
                            "token" to it.token,
                            "id" to it.id.toString(),
                            "username" to it.username
                        )
                    )
                }.onFailure {
                    call.respond(HttpStatusCode.BadRequest, it.localizedMessage)
                }
            }

            post("create", {
                summary = "Register with [email + password] or [wallet address + ecosystem]"
                request {
                    body<EmailAccountRequest> {
                        example("E-mail + password", buildJsonObject {
                            put("username", JsonPrimitive("user@email.com"))
                            put("password", JsonPrimitive("password"))
                            put("type", JsonPrimitive("email"))
                        }.toString())
                        example("Wallet address + ecosystem", buildJsonObject {
                            put("address", JsonPrimitive("0xABC"))
                            put("ecosystem", JsonPrimitive("ecosystem"))
                            put("type", JsonPrimitive("address"))
                        }.toString())
                    }
                }
                response {
                    HttpStatusCode.Created to { description = "Register successful" }
                    HttpStatusCode.BadRequest to { description = "Register failed" }
                }
            }) {
                val req = LoginRequestJson.decodeFromString<AccountRequest>(call.receive())
                AccountsService.register(req).onSuccess {
                    println("Registration succeed.")
                    call.response.status(HttpStatusCode.Created)
                    call.respond("Registration succeed.")
                }.onFailure {
                    call.respond(HttpStatusCode.BadRequest, it.localizedMessage)
                }
            }

            authenticate("authenticated-session", "authenticated-bearer") {
                get("user-info", {
                    summary = "Return user ID if logged in"
                }) {
                    call.respond(getUserId().name)
                }
                get("session", {
                    summary = "Return session ID if logged in"
                }) {
                    //val token = getUserId().name
                    val token = getUsersSessionToken() ?: throw UnauthorizedException("Invalid session")

                    if (securityUserTokenMapping.contains(token))
                        call.respond(mapOf("token" to mapOf("accessToken" to token)))
                    else throw UnauthorizedException("Invalid (outdated?) session!")
                }
            }

            post("logout", {
                summary = "Logout (delete session)"
                response { HttpStatusCode.OK to { description = "Logged out." } }
            }) {
                val token = getUsersSessionToken()

                securityUserTokenMapping.remove(token)

                call.sessions.clear<LoginTokenSession>()
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}


fun PipelineContext<Unit, ApplicationCall>.getUserId() = call.principal<UserIdPrincipal>("authenticated-session")
    ?: call.principal<UserIdPrincipal>("authenticated-bearer")
    ?: throw UnauthorizedException("Could not retrieve authorized user.")

fun PipelineContext<Unit, ApplicationCall>.getUserUUID() =
    runCatching { UUID(getUserId().name) }
        .getOrNull() ?: throw IllegalArgumentException("Invalid user id")

fun PipelineContext<Unit, ApplicationCall>.getWalletId() =
    runCatching {
        UUID(call.parameters["wallet"] ?: throw IllegalArgumentException("No wallet ID provided"))
    }.getOrElse { throw IllegalArgumentException("Invalid wallet ID provided") }

fun PipelineContext<Unit, ApplicationCall>.getWalletService(walletId: UUID) =
    WalletServiceManager.getWalletService(getUserUUID(), walletId)

fun PipelineContext<Unit, ApplicationCall>.getWalletService() =
    WalletServiceManager.getWalletService(getUserUUID(), getWalletId())

fun PipelineContext<Unit, ApplicationCall>.getUsersSessionToken(): String? =
    call.sessions.get(LoginTokenSession::class)?.token ?: call.request.authorization()
        ?.removePrefix("Bearer ")

fun getNftService() = WalletServiceManager.getNftService()
