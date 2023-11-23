package id.walt.service.credentials

import id.walt.db.models.AccountCredentials
import id.walt.db.models.Accounts
import id.walt.db.models.Credentials
import id.walt.db.repositories.AccountCredentialsRepository
import id.walt.db.repositories.CredentialsRepository
import id.walt.db.repositories.DbAccountCredentials
import id.walt.db.repositories.DbCredential
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import java.util.*

//TODO: replace DbCredential with a dto
object CredentialsService {
    fun get(account: UUID, id: String): DbCredential? =
        list(account).singleOrNull { it.id == UUID.fromString(id) }

    fun list(account: UUID): List<DbCredential> = join(account).let {
        AccountCredentialsRepository.query(it) {
            DbCredential(
                id = it[Credentials.id].value,
                credentialId = it[Credentials.credentialId],
                document = it[Credentials.document]
            )
        }
    }

    fun add(account: UUID, credential: DbCredential): UUID = CredentialsRepository.insert(
        DbCredential(
            credentialId = credential.credentialId,
            document = credential.document,
        )
    ).let { cid ->
        join(account, cid).let {
            AccountCredentialsRepository.query(it) {
                it[AccountCredentials.id]
            }
        }.takeIf { it.isNotEmpty() }?.single()?.value ?: let {
            AccountCredentialsRepository.insert(
                DbAccountCredentials(
                    account = account,
                    credential = cid,
                )
            )
        }
    }

    fun delete(account: UUID, id: String): Boolean = join(account, UUID.fromString(id)).let {
        AccountCredentialsRepository.query(it) {
            it[AccountCredentials.id]
        }.singleOrNull()?.value
    }?.let {
        AccountCredentialsRepository.delete(it)
    }?.let { it > 0 } ?: false

    fun update(account: UUID, credential: DbCredential): Boolean {
        TODO()
    }

    private fun join(account: UUID, id: UUID? = null) = Accounts.innerJoin(AccountCredentials,
        onColumn = { Accounts.id },
        otherColumn = { AccountCredentials.account },
        additionalConstraint = {
            Accounts.id eq account
        }).innerJoin(Credentials,
        onColumn = { Credentials.id },
        otherColumn = { AccountCredentials.credential },
        additionalConstraint = id?.let {
            {
                Credentials.id eq id
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
                credentialId = credentialId,
                document = document,
            )
        )
    }
}