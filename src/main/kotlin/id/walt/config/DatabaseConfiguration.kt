package id.walt.config

import com.zaxxer.hikari.HikariDataSource

data class DatabaseConfiguration(
    val database: String,
    val recreate_schema: Boolean = false
) : WalletConfig

data class DatasourceConfiguration(
    val hikariDataSource: HikariDataSource
) : WalletConfig
