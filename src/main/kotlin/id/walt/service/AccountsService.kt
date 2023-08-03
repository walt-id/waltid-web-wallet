package id.walt.service

import de.mkammerer.argon2.Argon2Factory
import id.walt.db.models.AccountWallets
import id.walt.db.models.Accounts
import id.walt.db.models.Emails
import id.walt.db.models.Wallets
import id.walt.web.ByteLoginRequest
import id.walt.web.UnauthorizedException
import id.walt.web.generateToken
import id.walt.web.model.AddressLoginRequest
import id.walt.web.model.EmailLoginRequest
import id.walt.web.model.LoginRequest
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object AccountsService {

    fun register(request: LoginRequest): Result<RegistrationResult> = when (request) {
        is EmailLoginRequest -> register(ByteLoginRequest(request))
        is AddressLoginRequest -> register(request)
    }

    fun authenticate(request: LoginRequest): Result<AuthenticationResult> = runCatching {
        when (request) {
            is EmailLoginRequest -> authenticate(ByteLoginRequest(request))
            is AddressLoginRequest -> authenticate(request)
        }
    }.fold(onSuccess = { Result.success(AuthenticationResult(id = it.id, username = it.username, token = generateToken())) },
        onFailure = { Result.failure(it) })

    //TODO: implement registration strategies
    //region -Registration-
    private fun register(request: ByteLoginRequest): Result<RegistrationResult> = runCatching {
        val hash = hashPassword(request.password)
        transaction {
            (Accounts innerJoin Emails).select { Emails.email eq request.username }.takeIf {
                it.none()
            }?.let {
                Emails.insertAndGetId {
                    it[email] = request.username
                    it[password] = hash
                }.value
            }?.let { uuid ->
                Accounts.insertAndGetId { it[email] = uuid }.value
            }
        } ?: throw IllegalArgumentException("Account already exists.")
    }.fold(onSuccess = {
        Result.success(RegistrationResult(it))
    }, onFailure = { Result.failure(it) })

    private fun register(request: AddressLoginRequest): Result<RegistrationResult> = runCatching {
        transaction {
            (Accounts innerJoin Wallets).select { Wallets.address eq request.address }.takeIf {
                it.none()
            }?.let {
                Wallets.insertAndGetId {
                    it[this.address] = request.address
                    it[ecosystem] = request.ecosystem
                }.value
            }?.let { walletId ->
                Accounts.insertAndGetId { it[wallet] = walletId }.value.also { accountId ->
                    AccountWallets.insert {
                        it[account] = accountId
                        it[wallet] = walletId
                    }
                }
            }
        } ?: throw IllegalArgumentException("Account already exists.")
    }.fold(onSuccess = {
        Result.success(RegistrationResult(it))
    }, onFailure = {
        Result.failure(it)
    })
    //endregion

    //TODO: implement authentication strategies
    //region -Authentication-
    private fun authenticate(request: ByteLoginRequest): AuthenticatedUser {
        val query = Emails.select { Emails.email eq request.username }
        val hasUser = transaction { query.count() > 0 }

        if (!hasUser)
            throw UnauthorizedException("Unknown user \"${request.username}\".")

        val user = transaction { query.first() }
        val pwHash = user[Emails.password]

        val passwordMatches = Argon2Factory.create().run {
            verify(pwHash, request.password).also {
                wipeArray(request.password)
            }
        }
        if (passwordMatches) {
            val id = transaction { Accounts.select { Accounts.email eq user[Emails.id].value }.first() }.let {
                it[Accounts.id].value
            }
            return AuthenticatedUser(id, request.username)
        } else {
            throw UnauthorizedException("Invalid password for \"${request.username}\"!")
        }
    }

    private fun authenticate(request: AddressLoginRequest): AuthenticatedUser = let {
        transaction {
            (Accounts innerJoin Wallets).select { Wallets.address eq request.address }.takeIf {
                it.any()
            }?.first()?.let {
                it[Accounts.id].value
            }
        } ?: register(request).getOrThrow().id
    }.let { AuthenticatedUser(it, request.address) }
    //endregion

    private fun hashPassword(password: ByteArray) = Argon2Factory.create().run {
        hash(10, 65536, 1, password).also {
            wipeArray(password)
        }
    }
}

data class AuthenticationResult(
    val id: UUID,
    val username: String,
    val token: String,
)

data class RegistrationResult(
    val id: UUID,
)

data class AuthenticatedUser(
    val id: UUID,
    val username: String
)