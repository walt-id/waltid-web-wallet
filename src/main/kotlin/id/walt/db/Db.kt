package id.walt.db

import id.walt.config.ConfigManager
import id.walt.config.DatabaseConfiguration
import id.walt.config.DatasourceConfiguration
import io.github.oshai.kotlinlogging.KotlinLogging
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.bridge.SLF4JBridgeHandler
import java.sql.Connection

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
