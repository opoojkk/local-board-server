package com.joykeepsflowin.biz.user

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String
)