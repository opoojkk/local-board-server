package com.joykeepsflowin.auth

import com.auth0.jwt.JWT
import com.joykeepsflowin.db.dao.UserDao
import com.joykeepsflowin.util.JwtTokenConfig.AUDIENCE
import com.joykeepsflowin.util.JwtTokenConfig.EXPIRATION_TIME_MS
import com.joykeepsflowin.util.JwtTokenConfig.ISSUER
import com.joykeepsflowin.util.JwtTokenConfig.algorithm
import java.util.*

fun makeToken(user: UserDao): String {
    return JWT.create()
        .withIssuer(ISSUER)
        .withAudience(AUDIENCE)
        .withClaim("userId", user.id.value)
        .withExpiresAt(Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
        .sign(algorithm)
}