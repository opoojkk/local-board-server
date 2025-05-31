package com.joykeepsflowin.biz.bulletin

data class UpdateBulletinRequest(
    val title: String? = null,
    val content: String? = null,
    val isPinned: Boolean? = null
)
