package com.joykeepsflowin.db.entry

import org.jetbrains.exposed.sql.Table

object Bulletins : Table("bulletin") {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 100).default("")
    val content = text("content")
    val isPinned = bool("is_pinned").default(false)

    val createdAt = long("created_at").clientDefault { System.currentTimeMillis() }
    val updatedAt = long("updated_at").nullable()

    val createdBy = varchar("created_by", 50).nullable()

    override val primaryKey = PrimaryKey(id)
}