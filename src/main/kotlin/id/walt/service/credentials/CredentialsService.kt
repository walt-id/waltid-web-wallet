package id.walt.service.credentials

import id.walt.db.models.AccountCredentials
import id.walt.db.models.Accounts
import id.walt.db.models.Credentials
import id.walt.db.repositories.AccountCredentialsRepository
import id.walt.db.repositories.CredentialsRepository
import id.walt.db.repositories.DbAccountCredentials
import id.walt.db.repositories.DbCredential
import id.walt.service.dids.DidUpdateDataObject
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import java.net.URLEncoder
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
                        Credentials.credentialId eq credentialId
                    }
                }).selectAll()

    private fun find(credentialId: String) = Credentials.select { Credentials.credentialId eq credentialId }

    private fun getOrInsert(credentialId: String, document: String) = find(credentialId).let {
        CredentialsRepository.query(it) {
            it[Credentials.id]
        }.singleOrNull()?.value
    } ?: let {
        CredentialsRepository.insert(
            DbCredential(
                credentialId = URLEncoder.encode(credentialId, "UTF-8"),
                document = document,
            )
        )
    }
}