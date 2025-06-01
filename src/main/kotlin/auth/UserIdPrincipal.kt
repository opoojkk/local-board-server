package com.joykeepsflowin.auth

import io.ktor.server.auth.*


// 自定义 Principal 携带用户ID
data class UserIdPrincipal(
    val userId: String,      // 用户唯一标识
) : Principal


fun UserIdPrincipal.toClaims(): Map<String, Any> = mapOf(
    "id" to userId,
)