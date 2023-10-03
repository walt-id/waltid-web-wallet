package id.walt.service

import id.walt.db.models.WalletOperationHistory
import id.walt.service.dto.LinkedWalletDataTransferObject
import id.walt.service.dto.WalletDataTransferObject
import id.walt.utils.JsonUtils.toJsonPrimitives
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.util.*

abstract class WalletService(val accountId: UUID) {

    // Credentials
    abstract suspend fun listCredentials(): List<Credential>
    abstract suspend fun listRawCredentials(): List<String>
    abstract suspend fun deleteCredential(id: String): Boolean
    abstract suspend fun getCredential(credentialId: String): String

    // SIOP
    abstract suspend fun usePresentationRequest(request: String, did: String): String?
    abstract suspend fun resolvePresentationRequest(request: String): String
    abstract suspend fun useOfferRequest(offer: String, did: String)

    // DIDs
    abstract suspend fun listDids(): List<String>
    abstract suspend fun loadDid(did: String): JsonObject
    abstract suspend fun createDid(method: String, args: Map<String, JsonPrimitive> = emptyMap()): String
    abstract suspend fun deleteDid(did: String): Boolean
    suspend fun createDidWithParameters(method: String, args: Map<String, Any?>): String {
        return createDid(method, args.toJsonPrimitives())
    }

    // Keys
    abstract suspend fun listKeys(): List<SingleKeyResponse>
    abstract suspend fun exportKey(alias: String, format: String, private: Boolean): String
    abstract suspend fun loadKey(alias: String): JsonObject
    abstract suspend fun importKey(jwkOrPem: String): String
    abstract suspend fun deleteKey(alias: String): Boolean

    // History
    abstract suspend fun getHistory(limit: Int = 10, offset: Int = 0): List<WalletOperationHistory>
    abstract suspend fun addOperationHistory(operationHistory: WalletOperationHistory)

    // Web3 wallets
    abstract suspend fun linkWallet(wallet: WalletDataTransferObject): LinkedWalletDataTransferObject
    abstract suspend fun unlinkWallet(wallet: UUID): Boolean
    abstract suspend fun getLinkedWallets(): List<LinkedWalletDataTransferObject>
    abstract suspend fun connectWallet(walletId: UUID): Boolean
    abstract suspend fun disconnectWallet(wallet: UUID): Boolean


    // TODO: Push
    // TODO: SIOP mid steps

}
