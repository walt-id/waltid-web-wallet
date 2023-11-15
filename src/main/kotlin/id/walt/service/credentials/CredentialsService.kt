package id.walt.service.credentials

import id.walt.db.models.WalletCredential
import id.walt.db.models.WalletCredentials
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import kotlinx.uuid.UUID
import kotlinx.uuid.toJavaUUID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

object CredentialsService {
    fun get(wallet: UUID, credentialId: String): WalletCredential? =
        WalletCredentials.select { (WalletCredentials.wallet eq wallet.toJavaUUID()) and (WalletCredentials.id eq credentialId) }
            .singleOrNull()?.let { WalletCredential(it) }

    fun list(wallet: UUID) = WalletCredentials.select { WalletCredentials.wallet eq wallet.toJavaUUID() }
        .map { WalletCredential(it) }

    fun add(wallet: UUID, credential: WalletCredential): String =
        WalletCredentials.insert {
            it[WalletCredentials.wallet] = wallet.toJavaUUID()
            it[id] = credential.id
            it[document] = credential.document
            it[disclosures] = credential.disclosures
            it[addedOn] = Clock.System.now().toJavaInstant()
        }[WalletCredentials.id]

    fun delete(wallet: UUID, credentialId: String): Boolean =
        WalletCredentials.deleteWhere { (WalletCredentials.wallet eq wallet.toJavaUUID()) and (id eq credentialId) } > 0

    /*fun update(account: UUID, did: DidUpdateDataObject): Boolean {
        TO-DO
    }*/
}
