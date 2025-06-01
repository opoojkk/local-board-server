package com.joykeepsflowin.module

import com.joykeepsflowin.db.entry.Bulletins
import com.joykeepsflowin.db.entry.UserTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDB() {
    // 1. 配置 Hikari 连接池
    val config = HikariConfig().apply {
        val envConfig = environment.config
        jdbcUrl = envConfig.tryGetString("ktor.db.jdbcUrl")
        username = envConfig.tryGetString("ktor.db.username")
        password = envConfig.tryGetString("ktor.db.password")
        maximumPoolSize = envConfig.tryGetString("ktor.db.maximumPoolSize")?.toInt() ?: 6  // 根据服务器核心数调整
        connectionTimeout = envConfig.tryGetString("ktor.db.connectionTimeout")?.toLong() ?: 30000L
        driverClassName = envConfig.tryGetString("ktor.db.driverClassName")
    }

    // 2. 创建数据源
    val dataSource = HikariDataSource(config)

    // 3. 连接 Exposed ORM
    Database.connect(dataSource)
    createTable()
}

fun createTable() {
    transaction {
        // 明确指定模式名（board）
        SchemaUtils.create(Bulletins)
        SchemaUtils.create(UserTable)
    }

}