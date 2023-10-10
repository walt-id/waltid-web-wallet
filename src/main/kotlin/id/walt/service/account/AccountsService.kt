package id.walt.service.account

import id.walt.db.models.Wallets
import id.walt.service.WalletServiceManager
import id.walt.web.generateToken
import id.walt.web.model.AddressLoginRequest
import id.walt.web.model.EmailLoginRequest
import id.walt.web.model.LoginRequest
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object AccountsService {
    private val emailStrategy = EmailStrategy()
    private val walletStrategy = WalletStrategy()
    fun register(request: LoginRequest): Result<RegistrationResult> = when (request) {
        is EmailLoginRequest -> emailStrategy.register(request)
        is AddressLoginRequest -> walletStrategy.register(request)
    }.also {
        it.getOrNull()?.let {
            runBlocking { WalletServiceManager.getWalletService(it.id).createDid(method="key", alias = "Onboarding") }
        }
    }

    fun authenticate(request: LoginRequest): Result<AuthenticationResult> = runCatching {
        when (request) {
            is EmailLoginRequest -> emailStrategy.authenticate(request)
            is AddressLoginRequest -> walletStrategy.authenticate(request)
        }
    }.fold(onSuccess = {
        Result.success(
            AuthenticationResult(
                id = it.id,
                username = it.username,
                token = generateToken()
            )
        )
    },
        onFailure = { Result.failure(it) })
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

interface AccountStrategy<in T : LoginRequest> {
    fun register(request: T): Result<RegistrationResult>
    fun authenticate(request: T): AuthenticatedUser
}
