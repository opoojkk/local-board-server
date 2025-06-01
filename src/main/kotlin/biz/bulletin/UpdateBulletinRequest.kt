package com.joykeepsflowin.biz.bulletin

import kotlinx.serialization.Serializable

@Serializable
data class UpdateBulletinRequest(
    val title: String? = null,
    val content: String? = null,
    val isPinned: Boolean? = null
)
