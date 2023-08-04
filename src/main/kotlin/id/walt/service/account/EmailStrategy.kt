package id.walt.service.account

import de.mkammerer.argon2.Argon2Factory
import id.walt.db.models.Accounts
import id.walt.db.models.Emails
import id.walt.web.ByteLoginRequest
import id.walt.web.UnauthorizedException
import id.walt.web.model.EmailLoginRequest
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class EmailStrategy: AccountStrategy<EmailLoginRequest> {
    override fun register(request: EmailLoginRequest): Result<RegistrationResult> = runCatching {
        val hash = hashPassword(ByteLoginRequest(request).password)
        transaction {
            (Accounts innerJoin Emails).select { Emails.email eq request.username }.takeIf {
                it.none()
            }?.let {
                getOrInsertEmailAddress(request.username, hash)
            }?.let { uuid ->
                Accounts.insertAndGetId { it[email] = uuid }.value
            }
        } ?: throw IllegalArgumentException("Account already exists.")
    }.fold(onSuccess = {
        Result.success(RegistrationResult(it))
    }, onFailure = { Result.failure(it) })

    override fun authenticate(request: EmailLoginRequest): AuthenticatedUser = ByteLoginRequest(request).let { req ->
        val query = Emails.select { Emails.email eq req.username }
        val hasUser = transaction { query.count() > 0 }

        if (!hasUser)
            throw UnauthorizedException("Unknown user \"${req.username}\".")

        val user = transaction { query.first() }
        val pwHash = user[Emails.password]

        val passwordMatches = Argon2Factory.create().run {
            verify(pwHash, req.password).also {
                wipeArray(req.password)
            }
        }
        if (passwordMatches) {
            val id = transaction { Accounts.select { Accounts.email eq user[Emails.id].value }.first() }.let {
                it[Accounts.id].value
            }
            return AuthenticatedUser(id, req.username)
        } else {
            throw UnauthorizedException("Invalid password for \"${req.username}\"!")
        }
    }

    private fun hashPassword(password: ByteArray) = Argon2Factory.create().run {
        hash(10, 65536, 1, password).also {
            wipeArray(password)
        }
    }

    private fun getOrInsertEmailAddress(email: String, password: String): UUID = transaction {
        Emails.select { Emails.email eq email }.firstOrNull()?.let {
            it[Emails.id]
        }?.value ?: let {
            Emails.insertAndGetId {
                it[Emails.email] = email
                it[Emails.password] = password
            }.value
        }
    }
}