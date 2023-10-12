package id.walt.service

import id.walt.crypto.keys.Key
import id.walt.crypto.keys.KeySerialization
import id.walt.crypto.keys.KeyType
import id.walt.crypto.keys.LocalKey
import id.walt.db.models.*
import id.walt.did.dids.DidService
import id.walt.did.dids.registrar.dids.DidCheqdCreateOptions
import id.walt.did.dids.registrar.dids.DidJwkCreateOptions
import id.walt.did.dids.registrar.dids.DidKeyCreateOptions
import id.walt.did.dids.registrar.dids.DidWebCreateOptions
import id.walt.did.helpers.WaltidServices
import id.walt.did.utils.EnumUtils.enumValueIgnoreCase
import id.walt.oid4vc.data.GrantType
import id.walt.oid4vc.data.OpenIDProviderMetadata
import id.walt.oid4vc.providers.OpenIDClientConfig
import id.walt.oid4vc.providers.SIOPProviderConfig
import id.walt.oid4vc.requests.AuthorizationRequest
import id.walt.oid4vc.requests.CredentialOfferRequest
import id.walt.oid4vc.requests.CredentialRequest
import id.walt.oid4vc.requests.TokenRequest
import id.walt.oid4vc.responses.CredentialResponse
import id.walt.oid4vc.responses.TokenResponse
import id.walt.oid4vc.util.randomUUID
import id.walt.service.dto.LinkedWalletDataTransferObject
import id.walt.service.dto.WalletDataTransferObject
import id.walt.service.keys.KeysService
import id.walt.service.oidc4vc.TestCredentialWallet
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URLDecoder
import java.util.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Duration.Companion.seconds


class SSIKit2WalletService(accountId: UUID) : WalletService(accountId) {

    companion object {
        init {
            runBlocking {
                WaltidServices.init()
            }
        }
    }

    private val userEmail: String by lazy {
        transaction {
            Accounts.select { Accounts.id eq accountId }.single().let { acc ->
                Emails.select { Emails.id eq acc[Accounts.email]?.value }.single()[Emails.email]
            }
        }
    }

    /* Credentials */

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun listCredentials(): List<Credential> =
        transaction {
            WalletCredentials.select { WalletCredentials.account eq accountId }.map {
                it
            }
        }.mapNotNull { resultRow ->
            val credentialId = resultRow[WalletCredentials.credentialId]
            runCatching {
                val cred = resultRow[WalletCredentials.credential]
                val parsedCred = if (cred.startsWith("{"))
                    Json.parseToJsonElement(cred).jsonObject
                else if (cred.startsWith("ey"))
                    Json.parseToJsonElement(
                        Base64.decode(cred.split(".")[1]).decodeToString()
                    ).jsonObject["vc"]!!.jsonObject
                else throw IllegalArgumentException("Unknown credential format")
                Credential(parsedCred, cred)
            }.onFailure { it.printStackTrace() }.getOrNull()?.let { cred ->
                Credential(JsonObject(cred.parsedCredential.toMutableMap().also {
                    it.putIfAbsent("id", JsonPrimitive(credentialId))
                }), cred.rawCredential)
            }
        }

    override suspend fun listRawCredentials(): List<String> {
        return transaction {
            WalletCredentials.select { WalletCredentials.account eq accountId }.map {
                it[WalletCredentials.credential]
            }
        }
    }

    override suspend fun deleteCredential(id: String) = transaction {
        WalletCredentials.deleteWhere { (account eq accountId) and (credentialId eq id) }
    } > 0

    override suspend fun getCredential(credentialId: String): String {
        return transaction {
            WalletCredentials.select { (WalletCredentials.account eq accountId) and (WalletCredentials.credentialId eq credentialId) }
                .single()[WalletCredentials.credential]
        }
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
        val credentialWallet = getCredentialWallet(did)

        val authReq = AuthorizationRequest.fromHttpQueryString(Url(request).encodedQuery)
        println("Auth req: $authReq")
        val walletSession = credentialWallet.initializeAuthorization(authReq, 60.seconds)
        println("Resolved presentation definition: ${walletSession.authorizationRequest!!.presentationDefinition!!.toJSONString()}")
        val tokenResponse = credentialWallet.processImplicitFlowAuthorization(walletSession.authorizationRequest!!)
        val resp = ktorClient.submitForm(walletSession.authorizationRequest!!.responseUri!!,
            parameters {
                tokenResponse.toHttpParameters().forEach { entry ->
                    entry.value.forEach { append(entry.key, it) }
                }
            })
        println(resp)

        if (!resp.status.isSuccess()) {
            return "https://error.org/"
        } else {
            return ""
        }
    }

    override suspend fun resolvePresentationRequest(request: String): String {
        val credentialWallet = getAnyCredentialWallet()

        return Url(request).protocolWithAuthority.plus("?").plus(credentialWallet.parsePresentationRequest(request).toHttpQueryString())
    }


    private val credentialWallets = HashMap<String, TestCredentialWallet>()

    fun getCredentialWallet(did: String) = credentialWallets.getOrPut(did) {
        TestCredentialWallet(
            SIOPProviderConfig("http://blank"),
            this,
            did
        )
    }
    fun getAnyCredentialWallet() = credentialWallets.values.firstOrNull() ?: getCredentialWallet("did:test:test")

    private val testCIClientConfig = OpenIDClientConfig("test-client", null, redirectUri = "http://blank")


    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun useOfferRequest(offer: String, did: String) {
        val credentialWallet = getCredentialWallet(did)

        println("// -------- WALLET ----------")
        println("// as WALLET: receive credential offer, either being called via deeplink or by scanning QR code")
        println("// parse credential URI")
        val parsedOfferReq = CredentialOfferRequest.fromHttpParameters(Url(offer).parameters.toMap())
        println("parsedOfferReq: $parsedOfferReq")

        println("// get issuer metadata")
        val providerMetadataUri =
            credentialWallet.getCIProviderMetadataUrl(parsedOfferReq.credentialOffer!!.credentialIssuer)
        println("Getting provider metadata from: $providerMetadataUri")
        val providerMetadataResult = ktorClient.get(providerMetadataUri)
        println("Provider metadata returned: " + providerMetadataResult.bodyAsText())

        val providerMetadata = providerMetadataResult.body<JsonObject>().let { OpenIDProviderMetadata.fromJSON(it) }
        println("providerMetadata: $providerMetadata")

        println("// resolve offered credentials")
        val offeredCredentials = parsedOfferReq.credentialOffer!!.resolveOfferedCredentials(providerMetadata)
        println("offeredCredentials: $offeredCredentials")

        val offeredCredential = offeredCredentials.first()
        println("offeredCredentials[0]: $offeredCredential")

        println("// fetch access token using pre-authorized code (skipping authorization step)")
        var tokenReq = TokenRequest(
            grantType = GrantType.pre_authorized_code,
            clientId = testCIClientConfig.clientID,
            redirectUri = credentialWallet.config.redirectUri,
            preAuthorizedCode = parsedOfferReq.credentialOffer!!.grants[GrantType.pre_authorized_code.value]!!.preAuthorizedCode,
            userPin = null
        )
        println("tokenReq: $tokenReq")

        var tokenResp = ktorClient.submitForm(
            providerMetadata.tokenEndpoint!!, formParameters = parametersOf(tokenReq.toHttpParameters())
        ).body<JsonObject>().let { TokenResponse.fromJSON(it) }
        println("tokenResp: $tokenResp")

        println(">>> Token response = success: ${tokenResp.isSuccess}")

        println("// receive credential")
        var nonce = tokenResp.cNonce

        println("Using issuer URL: ${parsedOfferReq.credentialOfferUri ?: parsedOfferReq.credentialOffer!!.credentialIssuer}")
        val credReq = CredentialRequest.forOfferedCredential(
            offeredCredential = offeredCredential,
            proof = credentialWallet.generateDidProof(
                did = credentialWallet.did,
                issuerUrl =  /*ciTestProvider.baseUrl*/ parsedOfferReq.credentialOfferUri
                    ?: parsedOfferReq.credentialOffer!!.credentialIssuer,
                nonce = nonce
            )
        )
        println("credReq: $credReq")

        val credentialResp = ktorClient.post(providerMetadata.credentialEndpoint!!) {
            contentType(ContentType.Application.Json)
            bearerAuth(tokenResp.accessToken!!)
            setBody(credReq.toJSON())
        }.body<JsonObject>().let { CredentialResponse.fromJSON(it) }
        println("credentialResp: $credentialResp")


        println("// parse and verify credential")
        if (credentialResp.credential == null) {
            throw IllegalStateException("No credential was returned from credentialEndpoint: $credentialResp")
        }

        val credential = credentialResp.credential!!.jsonPrimitive.content
        println(">>> CREDENTIAL IS: " + credential)

        val credentialId = Json.parseToJsonElement(Base64.decode(credential.split(".")[1]).decodeToString()).jsonObject["vc"]!!.jsonObject["id"]?.jsonPrimitive?.content ?: randomUUID()

        transaction {
            WalletCredentials.insert {
                it[WalletCredentials.account] = accountId
                it[WalletCredentials.credentialId] = credentialId
                it[WalletCredentials.credential] = credential
            }
        }
        println("Credential stored with Id: $credentialId")


    }

    /* DIDs */

    override suspend fun createDid(method: String, args: Map<String, JsonPrimitive>): String {
        // TODO: other DIDs
        val key = args["keyId"]?.content?.let { getKey(it) }?.getOrNull() ?: LocalKey.generate(KeyType.Ed25519)
        val keyId = key.getKeyId()
        val options = getDidOptions(method, args)

        val result = DidService.registerByKey(method, key, options)
        KeysService.insert(accountId, keyId, KeySerialization.serializeKey(key))

        transaction {

            WalletDids.insert {
                it[account] = accountId
                it[did] = result.did
                it[alias] = args["alias"]?.content ?: ""
                it[WalletDids.keyId] = keyId
                it[document] = Json.encodeToString(result.didDocument)
            }
        }

        return result.did
    }

    override suspend fun listDids(): List<Did> =
        transaction {
        WalletDids.select { WalletDids.account eq accountId }.map {resultRow ->
            Did(
                did = resultRow[WalletDids.did],
                alias = resultRow[WalletDids.alias],
                default = resultRow[WalletDids.default]
            )
        }
    }

    override suspend fun loadDid(did: String): JsonObject = Json.parseToJsonElement(transaction {
        WalletDids.select { (WalletDids.account eq accountId) and (WalletDids.did eq did) }
            .single()[WalletDids.document]
    }).jsonObject


    override suspend fun deleteDid(did: String): Boolean = transaction {
        WalletDids.deleteWhere { (account eq account) and (WalletDids.did eq did) }
    } > 0

    override suspend fun setDefault(did: String) = transaction{
        WalletDids.update({ WalletDids.default eq true }) {
            it[default] = false
    }
        WalletDids.update( {WalletDids.did eq did}){
            it[default] = true
        }
    } > 0

    /* Keys */

    private fun getKey(keyId: String) = runCatching {
        KeysService.getById(accountId, keyId)?.let {
            KeySerialization.deserializeKey(it).getOrThrow()
        } ?: throw IllegalArgumentException("Key not found: $keyId")
    }

    suspend fun getKeyByDid(did: String): Key = DidService.resolveToKey(did).fold(
        onSuccess = {
            getKey(it.getKeyId()).getOrElse { throw IllegalArgumentException("Could not deserialize resolved key: ${it.message}", it) }
        },
        onFailure = {
            throw it
        }
    )

    override suspend fun exportKey(alias: String, format: String, private: Boolean): String =
        getKey(alias).fold(onSuccess = {
            when (format.lowercase()) {
                "jwk" -> it.exportJWK()
                "pem" -> it.exportPEM()
                else -> throw IllegalArgumentException("Unknown format: $format")
            }
        }, onFailure = {
            throw it
        })

    override suspend fun loadKey(alias: String): JsonObject = getKey(alias).getOrThrow().exportJWKObject()

    override suspend fun listKeys(): List<SingleKeyResponse> =
        KeysService.getAllForAccount(accountId).map { (id, keyJson) ->
            val key = KeySerialization.deserializeKey(keyJson).getOrThrow()

            SingleKeyResponse(
                keyId = SingleKeyResponse.KeyId(id),
                algorithm = key.keyType.name,
                cryptoProvider = key.toString(),
                keyPair = JsonObject(emptyMap()),
                keysetHandle = JsonNull
            )
        }

    override suspend fun generateKey(type: String): String = LocalKey.generate(KeyType.valueOf(type)).let {
        KeysService.insert(accountId, it.getKeyId(), KeySerialization.serializeKey(it))
        it.getKeyId()
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
        val keyJson = KeySerialization.serializeKey(key)

        transaction {
            WalletKeys.insert {
                it[account] = accountId
                it[WalletKeys.keyId] = keyId
                it[document] = keyJson
            }
        }

        return keyId
    }

    override suspend fun deleteKey(alias: String): Boolean = transaction {
        WalletKeys.deleteWhere { (account eq accountId) and (keyId eq alias) }
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

    private fun getDidOptions(method: String, args: Map<String, JsonPrimitive>) = when (method.lowercase()) {
        "key" -> DidKeyCreateOptions(
            args["key"]?.let { enumValueIgnoreCase<KeyType>(it.content) } ?: KeyType.Ed25519,
            args["useJwkJcsPub"]?.let { it.content.toBoolean() } ?: false
        )
        "jwk" -> DidJwkCreateOptions()
        "web" -> DidWebCreateOptions(domain = args["domain"]?.content ?: "", path = args["path"]?.content ?: "")
        "cheqd" -> DidCheqdCreateOptions(
            network = args["network"]?.content ?: "test",
            document = Json.decodeFromString<JsonObject>(args["document"]?.content ?: "")
        )
        else -> DidJwkCreateOptions()
    }
}
