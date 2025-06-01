package com.joykeepsflowin.biz.bulletin

import kotlinx.serialization.Serializable

@Serializable
data class CreateBulletinRequest(
    val title: String,
    val content: String,
    val isPinned: Boolean = false,
    val createdBy: String? = null // 可选：从用户会话中获取
)
