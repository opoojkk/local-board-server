package com.joykeepsflowin.module

import com.joykeepsflowin.db.entry.Bulletins
import com.joykeepsflowin.db.entry.UserTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDB() {
    // 1. 配置 Hikari 连接池
    val config = HikariConfig().apply {
        jdbcUrl = "jdbc:mysql://localhost:3306/board?allowPublicKeyRetrieval=TRUE&useSSL=FALSE&serverTimezone=UTC"
        username = "root"
        password = "ljd123456"
        maximumPoolSize = 10  // 根据服务器核心数调整
        connectionTimeout = 30000
        driverClassName = "com.mysql.cj.jdbc.Driver" // MySQL 8.0+ 驱动类
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