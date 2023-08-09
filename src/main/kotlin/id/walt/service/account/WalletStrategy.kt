package id.walt.service.account

import id.walt.db.models.AccountWallets
import id.walt.db.models.Accounts
import id.walt.db.models.Wallets
import id.walt.web.model.AddressLoginRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class WalletStrategy : AccountStrategy<AddressLoginRequest> {
    override fun register(request: AddressLoginRequest): Result<RegistrationResult> = runCatching {
        transaction {
            (Accounts innerJoin Wallets).select { Wallets.address eq request.address }.takeIf {
                it.none()
            }?.let {
                getOrInsertWalletAddress(request.address, request.ecosystem)
            }?.let { walletId ->
                Accounts.insertAndGetId { it[wallet] = walletId }.value.also { accountId ->
                    AccountWallets.insert {
                        it[account] = accountId
                        it[wallet] = walletId
                        it[owner] = true
                    }
                }
            }
        } ?: throw IllegalArgumentException("Account already exists.")
    }.fold(onSuccess = {
        Result.success(RegistrationResult(it))
    }, onFailure = {
        Result.failure(it)
    })

    override fun authenticate(request: AddressLoginRequest): AuthenticatedUser = let {
        transaction {
            join(request.address).firstOrNull()?.let {
                it[Accounts.id].value
//                it[Wallets.address]
            }
        } ?: AccountsService.register(request).getOrThrow().id
    }.let { AuthenticatedUser(it, request.address) }

    private fun getOrInsertWalletAddress(address: String, ecosystem: String): UUID = transaction {
        Wallets.select { Wallets.address eq address }.firstOrNull()?.let {
            it[Wallets.id]
        }?.value ?: let {
            Wallets.insertAndGetId {
                it[this.address] = address
                it[this.ecosystem] = ecosystem
            }.value
        }
    }

    private fun join(address: String) = Accounts
        .innerJoin(AccountWallets, onColumn = { Accounts.id }, otherColumn = { AccountWallets.account })
        .innerJoin(Wallets, onColumn = { Wallets.id }, otherColumn = { AccountWallets.wallet }, additionalConstraint = {
            Wallets.address eq address and (AccountWallets.owner eq true)
        }).selectAll()
}