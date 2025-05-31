package com.joykeepsflowin.biz.bulletin

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class BulletinDTO(
    val id: Int,
    val title: String,
    val content: String,
    val isPinned: Boolean,
    // 将时间戳转为易读格式（UTC+8）
    val createdAt: String,
    val updatedAt: String?,
    val createdBy: String?
) {
    companion object {
        // 从数据库行转换到 DTO
        fun fromRow(
            id: Int,
            title: String,
            content: String,
            isPinned: Boolean,
            createdAt: Long,
            updatedAt: Long?,
            createdBy: String?
        ): BulletinDTO {
            return BulletinDTO(
                id = id,
                title = title,
                content = content,
                isPinned = isPinned,
                createdAt = formatTime(createdAt),
                updatedAt = updatedAt?.let { formatTime(it) },
                createdBy = createdBy
            )
        }

        private fun formatTime(timestamp: Long): String {
            return Instant.fromEpochMilliseconds(timestamp)
                .toLocalDateTime(TimeZone.of("Asia/Shanghai"))
                .toString() // 格式：2023-08-20T14:30:00
        }
    }
}
