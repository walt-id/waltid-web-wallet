package id.walt.service.credentials

import id.walt.db.repositories.DbCredential
import java.util.*

//TODO: replace DbCredential with a dto
object CredentialsService {
    fun get(account: UUID, credentialId: String): DbCredential {
        TODO()
    }

    fun list(account: UUID): List<DbCredential> {
        TODO()
    }

    fun add(account: UUID, credential: DbCredential) {
        TODO()
    }

    fun delete(account: UUID, credential: String): Boolean {
        TODO()
    }
}