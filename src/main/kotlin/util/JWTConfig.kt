package com.joykeepsflowin.util

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

object JWTConfig {
    private const val SECRET = "your_256bit_secret"  // 生产环境用环境变量替换
    private const val ISSUER = "your_issuer"
    val verifier: JWTVerifier = JWT
        .require(Algorithm.HMAC256(SECRET))
        .withIssuer(ISSUER)
        .build()

    fun generateToken(userId: String): String = JWT.create()
        .withSubject(userId)
        .withIssuer(ISSUER)
        .withClaim("id", userId)  // 自定义声明
        .withExpiresAt(Date(System.currentTimeMillis() + 86_400_000)) // 24小时过期
        .sign(Algorithm.HMAC256(SECRET))
}
