package com.joykeepsflowin.biz.bulletin

import kotlinx.serialization.Serializable

@Serializable
data class BulletinAddResponse(
    val data: List<BulletinDTO>,
    val total: Int,
    val page: Int,
    val pageSize: Int
)