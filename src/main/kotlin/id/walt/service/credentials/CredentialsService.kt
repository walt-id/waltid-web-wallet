package id.walt.service.credentials

import id.walt.config.ConfigManager
import id.walt.config.DatasourceConfiguration
import id.walt.db.models.AccountCredentials
import id.walt.db.models.Accounts
import id.walt.db.models.Credentials
import id.walt.db.repositories.*
import id.walt.service.dids.DidUpdateDataObject
import id.walt.service.dids.DidsService
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.selectAll
import java.util.*

//TODO: replace DbCredential with a dto
object CredentialsService {
    fun get(account: UUID, credentialId: String): DbCredential? =
        list(account).singleOrNull { it.credentialId == credentialId }

    fun list(account: UUID): List<DbCredential> = join(account).let {
        AccountCredentialsRepository.query(it) {
            DbCredential(
                id = it[Credentials.id].value,
                credentialId = it[Credentials.credentialId],
                document = it[Credentials.document]
            )
        }
    }

    fun add(account: UUID, credential: DbCredential): UUID =
        getOrInsert(credential.credentialId, credential.document).let { cid ->
            join(account, credential.credentialId).let {
                AccountCredentialsRepository.query(it) {
                    it[AccountCredentials.id]
                }
            }.takeIf { it.isNotEmpty() }?.single()?.value ?: let {
                AccountCredentialsRepository.insert(DbAccountCredentials(
                    account = account,
                    credential = cid,
                ))
            }
        }

    fun delete(account: UUID, credentialId: String): Boolean = join(account, credentialId).let {
        AccountCredentialsRepository.query(it) {
            it[AccountCredentials.id]
        }.singleOrNull()?.value
    }?.let {
        AccountCredentialsRepository.delete(it)
    }?.let { it > 0 } ?: false

    fun update(account: UUID, did: DidUpdateDataObject): Boolean {
        TODO()
    }

    private fun join(account: UUID, credentialId: String? = null) =
        Accounts.innerJoin(AccountCredentials, onColumn = { Accounts.id }, otherColumn = { AccountCredentials.account })
            .innerJoin(Credentials,
                onColumn = { Credentials.id },
                otherColumn = { AccountCredentials.credential },
                additionalConstraint = credentialId?.let {
                    {
                        Credentials.credentialId eq credentialId and (Accounts.id eq account)
                    }
                }).selectAll()

    private fun getOrInsert(credentialId: String, document: String) = CredentialsRepository.find(Credentials.credentialId, credentialId).takeIf {
        it.isNotEmpty()
    }?.single()?.id ?: let {
        CredentialsRepository.insert(DbCredential(
            credentialId = credentialId,
            document = document,
        ))
    }
}

fun main() {
    ConfigManager.loadConfigs(emptyArray())
    val datasourceConfig = ConfigManager.getConfig<DatasourceConfiguration>()
    Database.connect(datasourceConfig.hikariDataSource)
//    AccountsService.register(EmailLoginRequest("username", "password"))
//    KeysRepository.insert(DbKey(keyId = "keyId", document = "key-jwk"))
    val key = UUID.fromString("3d20dcc4-0988-4f7a-aff7-28a268c1ccb3")
    val account = UUID.fromString("b4746506-fb50-4f0d-9425-d688999e066c")
    val did = DidsService.get(account, "did")
    println(did)
    val res = CredentialsRepository.find(Credentials.credentialId, "credentialId")
    println(res)
    val cid = CredentialsService.add(account, DbCredential(
        credentialId = "credentialId",
        document = "document"
    ))
    println(cid)
}