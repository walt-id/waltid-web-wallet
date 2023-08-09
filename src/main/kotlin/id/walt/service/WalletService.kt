package id.walt.service

import id.walt.db.models.WalletOperationHistory
import id.walt.service.dto.WatchedWalletDataTransferObject
import id.walt.service.dto.WalletDataTransferObject
import id.walt.utils.JsonUtils.toJsonPrimitives
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.util.*

abstract class WalletService(val accountId: UUID) {

    // Credentials
    abstract suspend fun listCredentials(): List<JsonObject>
    abstract suspend fun deleteCredential(id: String): HttpResponse
    abstract suspend fun getCredential(credentialId: String): String

    // SIOP
    abstract suspend fun usePresentationRequest(request: String, did: String)
    abstract suspend fun useOfferRequest(offer: String, did: String)

    // DIDs
    abstract suspend fun listDids(): List<String>
    abstract suspend fun loadDid(did: String): JsonObject
    abstract suspend fun createDid(method: String, args: Map<String, JsonPrimitive> = emptyMap()): String
    suspend fun createDidWithParameters(method: String, args: Map<String, Any?>): String {
        return createDid(method, args.toJsonPrimitives())
    }

    // Keys
    abstract suspend fun listKeys(): List<WalletKitWalletService.SingleKeyResponse>
    abstract suspend fun exportKey(alias: String, format: String, private: Boolean): String
    abstract suspend fun importKey(jwkOrPem: String): String
    abstract suspend fun deleteKey(alias: String): HttpStatusCode
    abstract suspend fun deleteDid(did: String)
    abstract suspend fun getHistory(limit: Int = 10, offset: Int = 0): List<WalletOperationHistory>
    abstract suspend fun addOperationHistory(operationHistory: WalletOperationHistory)

    // Web3 wallets
    abstract suspend fun watchWallet(wallet: WalletDataTransferObject): WatchedWalletDataTransferObject
    abstract suspend fun unwatchWallet(wallet: UUID): Unit
    abstract suspend fun getWatchedWallets(): List<WatchedWalletDataTransferObject>


    // TODO: Push
    // TODO: SIOP mid steps

}
