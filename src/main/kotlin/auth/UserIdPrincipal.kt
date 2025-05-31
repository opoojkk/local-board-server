package com.joykeepsflowin.auth

import io.ktor.server.auth.*


// 自定义 Principal 携带用户ID
data class UserIdPrincipal(
    val id: String,      // 用户唯一标识
    val name: String? = null,
    val roles: List<String> = emptyList()
) : Principal
