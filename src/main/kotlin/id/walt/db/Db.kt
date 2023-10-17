package id.walt.db

import id.walt.config.ConfigManager
import id.walt.config.DatabaseConfiguration
import id.walt.config.DatasourceConfiguration
import id.walt.db.models.*
import id.walt.db.repositories.DbCredential
import id.walt.db.repositories.DbDid
import id.walt.db.repositories.DbKey
import id.walt.service.Did
import id.walt.service.account.AccountsService
import id.walt.service.credentials.CredentialsService
import id.walt.service.dids.DidInsertDataObject
import id.walt.service.dids.DidsService
import id.walt.service.keys.KeysService
import id.walt.web.model.EmailLoginRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.bridge.SLF4JBridgeHandler
import java.sql.Connection
import java.util.*

object Db {

    private val log = KotlinLogging.logger { }

    private fun connect() {
        val datasourceConfig = ConfigManager.getConfig<DatasourceConfiguration>()
        val databaseConfig = ConfigManager.getConfig<DatabaseConfiguration>()

        //migrate
        Flyway.configure()
            .locations(databaseConfig.database.replace(".", "/"))
            .dataSource(datasourceConfig.hikariDataSource)
            .load()
            .migrate()

        // connect
        log.info { "Connecting to database at \"${datasourceConfig.hikariDataSource.jdbcUrl}\"..." }
        Database.connect(datasourceConfig.hikariDataSource)
        TransactionManager.manager.defaultIsolationLevel =
            toTransactionIsolationLevel(datasourceConfig.hikariDataSource.transactionIsolation)
    }

    fun start() {
        connect()

        SLF4JBridgeHandler.removeHandlersForRootLogger()
        SLF4JBridgeHandler.install()

        transaction {
            addLogger(StdOutSqlLogger)
        }
    }

    private fun toTransactionIsolationLevel(value: String): Int = when (value) {
        "TRANSACTION_NONE" -> Connection.TRANSACTION_NONE
        "TRANSACTION_READ_UNCOMMITTED" -> Connection.TRANSACTION_READ_UNCOMMITTED
        "TRANSACTION_READ_COMMITTED" -> Connection.TRANSACTION_READ_COMMITTED
        "TRANSACTION_REPEATABLE_READ" -> Connection.TRANSACTION_REPEATABLE_READ
        "TRANSACTION_SERIALIZABLE" -> Connection.TRANSACTION_SERIALIZABLE
        else -> Connection.TRANSACTION_SERIALIZABLE
    }
}
fun main(){
    ConfigManager.loadConfigs(emptyArray())
    Db.start()
//    val account = AccountsService.register(EmailLoginRequest("username", "password")).getOrThrow().id
    val account = UUID.fromString("04e595ac-7c48-4482-9ea6-8ae0981251c8")
    println(account)
    val key = KeysService.add(account, DbKey(keyId = "keyId", document = "document"))
    println(key)
    val did = DidsService.add(
        account, DidInsertDataObject(
            key = key,
            did = Did(did = "did", document = "document")
        )
    )
    println(did)
    val cid = CredentialsService.add(account, DbCredential(
        credentialId = "credentialId",
        document = "document"
    ))
    println(cid)
//    ConfigManager.loadConfigs(emptyArray())
//    val datasourceConfig = ConfigManager.getConfig<DatasourceConfiguration>()
//    Database.connect(datasourceConfig.hikariDataSource)
//    transaction {
//        SchemaUtils.create(
//            Accounts,
//            Emails,
//            Wallets,
//            AccountWallets,
//            WalletOperationHistories,
//            Keys,
//            Dids,
//            Credentials,
//            AccountKeys,
//            AccountDids,
//            AccountCredentials,
//        )
//    }
}