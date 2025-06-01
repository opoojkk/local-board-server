package com.joykeepsflowin.util

import com.auth0.jwt.algorithms.Algorithm

object JwtTokenConfig {
    const val SECRET = "bulletin-board" // 替换为安全密钥
    const val ISSUER = "your-issuer"
    const val AUDIENCE = "your-audience"
    const val EXPIRATION_TIME_MS = 3_600_000 * 24 * 7 // 7天

    val algorithm: Algorithm = Algorithm.HMAC256(SECRET)
}
