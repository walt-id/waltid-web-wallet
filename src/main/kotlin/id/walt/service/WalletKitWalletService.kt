package id.walt.service

import id.walt.config.ConfigManager
import id.walt.config.RemoteWalletConfig
import id.walt.db.models.Accounts
import id.walt.db.models.Emails
import id.walt.db.models.WalletOperationHistories
import id.walt.db.models.WalletOperationHistory
import id.walt.service.dto.WatchedWalletDataTransferObject
import id.walt.service.dto.WalletDataTransferObject
import id.walt.utils.JsonUtils.toJsonPrimitive
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class WalletKitWalletService(accountId: UUID) : WalletService(accountId) {

    private var token: Lazy<String> = lazy { runBlocking { auth() } }

    companion object {
        private val walletConfig = ConfigManager.getConfig<RemoteWalletConfig>()

        val http = HttpClient(CIO) {
            install(ContentNegotiation) { json() }
            // expectSuccess = false
            install(Logging) {
                level = LogLevel.BODY
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 2 * 60 * 1000
            }

            defaultRequest {
                url(walletConfig.remoteWallet)
            }
        }
    }

    private val userEmail by lazy {
        transaction {
            Accounts.select { Accounts.id eq accountId }.single().let { acc ->
                Emails.select { Emails.id eq acc[Accounts.email]?.value }.single()[Emails.email]
            }
        }
    }

    private fun HttpResponse.checkValid(resend: () -> HttpResponse): HttpResponse =
        if (status == HttpStatusCode.Unauthorized) {
            runBlocking { reauth() }
            resend.invoke()
        } else this

    private suspend fun authenticatedJsonGet(path: String, block: HttpRequestBuilder.() -> Unit = {}): HttpResponse =
        http.get(path) {
            bearerAuth(token.value)
            contentType(ContentType.Application.Json)
            block.invoke(this)
        }.checkValid { runBlocking { authenticatedJsonGet(path, block) } }

    private suspend fun authenticatedJsonDelete(path: String, block: HttpRequestBuilder.() -> Unit = {}): HttpResponse =
        http.delete(path) {
            bearerAuth(token.value)
            contentType(ContentType.Application.Json)
            block.invoke(this)
        }.checkValid { runBlocking { authenticatedJsonDelete(path, block) } }

    private suspend inline fun <reified T> authenticatedJsonPost(
        path: String,
        body: T,
        crossinline block: HttpRequestBuilder.() -> Unit = {}
    ): HttpResponse =
        http.post(path) {
            bearerAuth(token.value)
            contentType(ContentType.Application.Json)
            setBody(body)
            block.invoke(this)
        }.checkValid {
            runBlocking {
                http.post(path) {
                    bearerAuth(token.value)
                    contentType(ContentType.Application.Json)
                    setBody(body)
                    block.invoke(this)
                }
            }
        }

    @Serializable
    data class AuthRequest(val id: String, val password: String)

    private suspend fun reauth() {
        println("Token invalid - reauthenticating!")
        token = lazy { runBlocking { auth() } }
    }

    private suspend fun auth(): String =
        (http.post("/api/auth/login") {
            setBody(AuthRequest(userEmail, userEmail))
            contentType(ContentType.Application.Json)
        }.body<JsonObject>()["token"] ?: throw IllegalStateException("Could not login: $userEmail")
                ).jsonPrimitive.content

    /* Credentials */

    override suspend fun listCredentials() = authenticatedJsonGet("/api/wallet/credentials/list")
        .body<JsonObject>()["list"]!!.jsonArray.toList().map { it.jsonObject }

    //private val prettyJson = Json { prettyPrint = true }

    override suspend fun deleteCredential(id: String) = authenticatedJsonDelete("/api/wallet/credentials/delete/$id")

    override suspend fun getCredential(credentialId: String): String =
        /*prettyJson.encodeToString(*/listCredentials().first { it["id"]?.jsonPrimitive?.content == credentialId }.toString()//)
    /* override suspend fun getCredential(credentialId: String): String =
         authenticatedJsonGet("/api/wallet/credentials/$credentialId")
             .bodyAsText()*/


    /* SIOP */

    override suspend fun usePresentationRequest(request: String, did: String) {
        val sessionId = authenticatedJsonPost(
            "/api/wallet/presentation/startPresentation",
            mapOf("oidcUri" to request)
        ).bodyAsText()

        val presentableCredentials = authenticatedJsonGet("/api/wallet/presentation/continue") {
            url {
                parameters.apply {
                    append("sessionId", sessionId)
                    append("did", did)
                }
            }
        }.body<JsonObject>()["presentableCredentials"]!!.jsonArray

        val presentResp = authenticatedJsonPost("/api/wallet/presentation/fulfill", presentableCredentials) {
            url {
                parameters.apply {
                    append("sessionId", sessionId)
                }
            }
        }
        println(presentResp)
    }

    override suspend fun useOfferRequest(offer: String, did: String) {
        val sessionId = authenticatedJsonPost(
            "/api/wallet/issuance/startIssuerInitiatedIssuance",
            mapOf("oidcUri" to offer)
        ).bodyAsText()

        authenticatedJsonGet("/api/wallet/issuance/continueIssuerInitiatedIssuance") {
            url {
                parameters.apply {
                    append("sessionId", sessionId)
                    append("did", did)
                }
            }
        }
    }

    /* DIDs */

    override suspend fun createDid(method: String, args: Map<String, JsonPrimitive>): String {
        val createParams = mutableMapOf("method" to method.toJsonPrimitive())

        createParams.putAll(
            args.mapKeys {
                val k = it.key
                when {
                    method == "ebsi" && k == "bearerToken" -> "didEbsiBearerToken"
                    method == "ebsi" && k == "version" -> "didEbsiVersion"

                    method == "web" && k == "domain" -> "didWebDomain"
                    method == "web" && k == "path" -> "didWebPath"

                    else -> it.key
                }
            })

        return authenticatedJsonPost("/api/wallet/did/create", createParams).bodyAsText()
    }

    override suspend fun listDids() = authenticatedJsonGet("/api/wallet/did/list")
        .body<List<String>>()

    override suspend fun loadDid(did: String) = authenticatedJsonGet("/api/wallet/did/$did")
        .body<JsonObject>()

    override suspend fun deleteDid(did: String) {
        authenticatedJsonDelete("/api/wallet/did/delete/$did")
    }

    /* Keys */

    override suspend fun exportKey(alias: String, format: String, private: Boolean): String =
        authenticatedJsonPost(
            "/api/wallet/keys/export", mapOf(
                "keyAlias" to JsonPrimitive(alias),
                "format" to JsonPrimitive(format),
                "exportPrivate" to JsonPrimitive(private)
            ).toMutableMap()
        )
            .body<String>()

    @Serializable
    data class SingleKeyResponse(
        val algorithm: String,
        val cryptoProvider: String,
        val keyId: KeyId,
        val keyPair: JsonObject,
        val keysetHandle: JsonElement
    ) {
        @Serializable
        data class KeyId(val id: String)
    }

    override suspend fun listKeys() = authenticatedJsonGet("/api/wallet/keys/list")
        .body<JsonObject>()["list"]!!.jsonArray.map { Json.decodeFromJsonElement<SingleKeyResponse>(it) }

    override suspend fun importKey(jwkOrPem: String) =
        authenticatedJsonPost("/api/wallet/keys/import", body = jwkOrPem)
            .body<String>()

    override suspend fun deleteKey(alias: String) =
        authenticatedJsonDelete("/api/wallet/keys/delete/$alias").status


    fun addToHistory() {
        // data from
        // https://wallet.walt-test.cloud/api/wallet/issuance/info?sessionId=SESSION_ID
        // after taking up issuance offer
    }
    // TODO
    //fun infoAboutOfferRequest

    override suspend fun getHistory(limit: Int, offset: Int): List<WalletOperationHistory> = transaction {
        WalletOperationHistories
            .select { WalletOperationHistories.account eq accountId }
            .orderBy(WalletOperationHistories.timestamp)
            .limit(10)
            .map { row ->
                WalletOperationHistory(
                    accountId = row[WalletOperationHistories.account].value.toString(),
                    timestamp = row[WalletOperationHistories.timestamp].toKotlinInstant(),
                    operation = row[WalletOperationHistories.operation],
                    data = Json.parseToJsonElement(row[WalletOperationHistories.data]).jsonObject.toMap()
                )
            }
    }

    override suspend fun addOperationHistory(operationHistory: WalletOperationHistory) {
        transaction {
            WalletOperationHistories.insert {
                it[account] = UUID.fromString(operationHistory.accountId)
                it[timestamp] = operationHistory.timestamp.toJavaInstant()
                it[operation] = operationHistory.operation
                it[data] = Json.encodeToString(operationHistory.data)
            }
        }
    }

    override suspend fun watchWallet(wallet: WalletDataTransferObject): WatchedWalletDataTransferObject =
        Web3WalletService.watch(accountId, wallet)

    override suspend fun unwatchWallet(wallet: UUID) = Web3WalletService.unwatch(accountId, wallet)

    override suspend fun getWatchedWallets(): List<WatchedWalletDataTransferObject> = Web3WalletService.getWatching(accountId)
}
