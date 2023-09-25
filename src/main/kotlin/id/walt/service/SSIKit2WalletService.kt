package id.walt.service

import id.walt.core.crypto.keys.LocalKey
import id.walt.core.crypto.keys.LocalKey.Companion
import id.walt.core.crypto.keys.TSEKey
import id.walt.db.models.*
import id.walt.oid4vc.requests.CredentialOfferRequest
import id.walt.service.dto.LinkedWalletDataTransferObject
import id.walt.service.dto.WalletDataTransferObject
import id.walt.ssikit.did.registrar.dids.DidCreateOptions
import id.walt.ssikit.did.registrar.local.jwk.DidJwkRegistrar
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.*


class SSIKit2WalletService(accountId: UUID) : WalletService(accountId) {

    private val userEmail: String by lazy {
        transaction {
            Accounts.select { Accounts.id eq accountId }.single().let { acc ->
                Emails.select { Emails.id eq acc[Accounts.email]?.value }.single()[Emails.email]
            }
        }
    }

    private val walletId: UUID by lazy {
        transaction {
            AccountWallets.select { AccountWallets.account eq accountId }
                .first()[AccountWallets.wallet].value
        }
    }

    /* Credentials */

    override suspend fun listCredentials(): List<JsonObject> =
        transaction {
            WalletCredentials.select { WalletCredentials.wallet eq walletId }.map {
                it[WalletCredentials.credential]
            }
        }.map {
            Json.decodeFromString<JsonObject>(it)
        }

    override suspend fun deleteCredential(id: String) = transaction {
        WalletCredentials.deleteWhere { (wallet eq walletId) and (credentialId eq id) }
    } > 0

    override suspend fun getCredential(credentialId: String): String = transaction {
        WalletCredentials.select { (WalletCredentials.wallet eq walletId) and (WalletCredentials.credentialId eq id) }
            .single()[WalletCredentials.credential]
    }

    private fun getQueryParams(url: String): Map<String, MutableList<String>> {
        val params: MutableMap<String, MutableList<String>> = HashMap()
        val urlParts = url.split("\\?".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        if (urlParts.size <= 1)
            return params

        val query = urlParts[1]
        for (param in query.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            val pair = param.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val key = URLDecoder.decode(pair[0], "UTF-8")
            var value = ""
            if (pair.size > 1) {
                value = URLDecoder.decode(pair[1], "UTF-8")
            }
            var values = params[key]
            if (values == null) {
                values = ArrayList()
                params[key] = values
            }
            values.add(value)
        }
        return params
    }


    /* SIOP */
    @Serializable
    data class PresentationResponse(
        val vp_token: String,
        val presentation_submission: String,
        val id_token: String?,
        val state: String?,
        val fulfilled: Boolean,
        val rp_response: String?
    )

    @Serializable
    data class SIOPv2Response(
        val vp_token: String,
        val presentation_submission: String,
        val id_token: String?,
        val state: String?
    )

    private val ktorClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
        followRedirects = false
    }

    /**
     * @return redirect uri
     */
    override suspend fun usePresentationRequest(request: String, did: String): String {
        return ""
    }

    override suspend fun useOfferRequest(offer: String, did: String) {
        /*val parsedOfferReq = CredentialOfferRequest.fromHttpParameters(Url(offer).parameters.toMap())
        val providerMetadataUri = credentialWallet.getCIProviderMetadataUrl(parsedOfferReq.credentialOffer!!.credentialIssuer)
        val providerMetadata = ktorClient.get(providerMetadataUri).call.body<OpenIDProviderMetadata>()*/

    }

    /* DIDs */

    override suspend fun createDid(method: String, args: Map<String, JsonPrimitive>): String {
        // TODO: other DIDs
        val result = DidJwkRegistrar().register(DidCreateOptions(method, emptyMap()))

        transaction {
            WalletDids.insert {
                it[wallet] = walletId
                it[did] = result.did
                it[document] = Json.encodeToString(result.didDocument)
            }
        }

        return result.did
    }

    override suspend fun listDids(): List<String> = transaction {
        WalletDids.select { WalletDids.wallet eq walletId }.map {
            it[WalletDids.did]
        }
    }

    override suspend fun loadDid(did: String): JsonObject = Json.parseToJsonElement(transaction {
        WalletDids.select { (WalletDids.wallet eq walletId) and (WalletDids.did eq did) }.single()[WalletDids.document]
    }).jsonObject

    override suspend fun deleteDid(did: String): Boolean = transaction {
        WalletDids.deleteWhere { (wallet eq wallet) and (WalletDids.did eq did) }
    } > 0

    /* Keys */
    override suspend fun exportKey(alias: String, format: String, private: Boolean): String {
        val key = transaction {
            WalletKeys.select { (WalletKeys.wallet eq walletId) and (WalletKeys.keyId eq alias) }
                .single()[WalletKeys.document]
        }

        return when (format.lowercase()) {
            "jwk" -> key.exportJWK()
            "pem" -> key.exportPEM()
            else -> throw IllegalArgumentException("Unknown format: $format")
        }
    }

    override suspend fun listKeys(): List<SingleKeyResponse> {
        val keyList = transaction {
            WalletKeys.select { WalletKeys.wallet eq walletId }.map {
                Pair(it[WalletKeys.keyId], it[WalletKeys.document])
            }
        }

        return keyList.map { (id, key) ->
            SingleKeyResponse(
                keyId = SingleKeyResponse.KeyId(id),
                algorithm = key.keyType.name,
                cryptoProvider = key.toString(),
                keyPair = JsonObject(emptyMap()),
                keysetHandle = JsonNull
            )
        }
    }

    override suspend fun importKey(jwkOrPem: String): String {
        val type = when {
            jwkOrPem.lines().first().contains("BEGIN ") -> "pem"
            else -> "jwk"
        }

        val keyResult = when (type) {
            "pem" -> LocalKey.importPEM(jwkOrPem)
            "jwk" -> LocalKey.importJWK(jwkOrPem)
            else -> throw IllegalArgumentException("Unknown key type: $type")
        }

        if (keyResult.isFailure) {
            throw IllegalArgumentException("Could not import key as: $type; error message: " + keyResult.exceptionOrNull()?.message)
        }

        val key = keyResult.getOrThrow()
        val keyId = key.getKeyId()

        transaction {
            WalletKeys.insert {
                it[wallet] = walletId
                it[WalletKeys.keyId] = keyId
                it[document] = key
            }
        }

        return keyId
    }

    override suspend fun deleteKey(alias: String): Boolean = transaction {
        WalletKeys.deleteWhere { (wallet eq walletId) and (keyId eq alias) }
    } > 0

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

    override suspend fun linkWallet(wallet: WalletDataTransferObject): LinkedWalletDataTransferObject =
        Web3WalletService.link(accountId, wallet)

    override suspend fun unlinkWallet(wallet: UUID) = Web3WalletService.unlink(accountId, wallet)

    override suspend fun getLinkedWallets(): List<LinkedWalletDataTransferObject> =
        Web3WalletService.getLinked(accountId)

    override suspend fun connectWallet(walletId: UUID) = Web3WalletService.connect(accountId, walletId)

    override suspend fun disconnectWallet(wallet: UUID) = Web3WalletService.disconnect(accountId, wallet)
}
