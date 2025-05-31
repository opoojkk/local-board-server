package com.joykeepsflowin.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.joykeepsflowin.db.dao.UserDao
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.*

object JwtTokenHelper {
    private const val SECRET = "your-256-bit-secret" // 替换为安全密钥
    private const val ISSUER = "your-issuer"
    private const val AUDIENCE = "your-audience"
    private const val EXPIRATION_TIME_MS = 3_600_000 * 24 // 24小时

    val algorithm = Algorithm.HMAC256(SECRET)

    fun makeToken(user: UserDao): String {
        return JWT.create()
            .withIssuer(ISSUER)
            .withAudience(AUDIENCE)
            .withClaim("userId", user.id.value)
            .withExpiresAt(Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
            .sign(algorithm)
    }

//    fun Application.jwtConfigure() {
//        install(Authentication) {
//            jwt {
//                verifier(
//                    JWT.require(algorithm)
//                        .withIssuer(ISSUER)
//                        .build()
//                )
//                validate { credential ->
//                    val userId = credential.payload.getClaim("userId").asInt()
//                    UserDao.findById(userId)?.let { JWTPrincipal(credential.payload) }
//                }
//            }
//        }
//    }
}
