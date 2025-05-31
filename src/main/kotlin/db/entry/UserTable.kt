package com.joykeepsflowin.db.entry

import org.jetbrains.exposed.dao.id.IntIdTable

object UserTable : IntIdTable("users") {
    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex().nullable()
    val passwordHash = varchar("password_hash", 255)  // 存储 bcrypt/scrypt 哈希
    val createdAt = long("created_at").clientDefault { System.currentTimeMillis() }
    val updatedAt = long("updated_at").nullable()
    val isActive = bool("is_active").default(true)
}
