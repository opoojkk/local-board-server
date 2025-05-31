package com.joykeepsflowin.biz.user

import com.joykeepsflowin.db.dao.UserDao
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Int,
    val username: String,
    val email: String?
)

fun UserDao.toResponse() = UserResponse(
    id = this.id.value,
    username = this.username,
    email = this.email
)
