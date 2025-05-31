package com.joykeepsflowin.biz.bulletin

import com.joykeepsflowin.db.entry.Bulletins
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class BulletinService {
    fun createBulletin(title: String, content: String, createdBy: String) {
        transaction {
            // 插入时自动使用 UTC+8 时间（通过 clientDefault）
            Bulletins.insert {
                it[this.title] = title
                it[this.content] = content
                it[this.createdBy] = createdBy

            }
        }
    }

    fun listBulletins(): List<BulletinDTO> {
        return transaction {
            Bulletins.selectAll().map {
                BulletinDTO.fromRow(
                    it[Bulletins.id],
                    it[Bulletins.title],
                    it[Bulletins.content],
                    it[Bulletins.isPinned],
                    it[Bulletins.createdAt],
                    it[Bulletins.updatedAt],
                    it[Bulletins.createdBy]
                )
            }
        }
    }
}