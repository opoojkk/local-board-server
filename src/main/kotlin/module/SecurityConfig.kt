package com.joykeepsflowin.module

import com.auth0.jwt.JWT
import com.auth0.jwt.exceptions.JWTVerificationException
import com.joykeepsflowin.auth.UserIdPrincipal
import com.joykeepsflowin.db.dao.UserDao
import com.joykeepsflowin.util.JwtTokenConfig
import com.joykeepsflowin.util.JwtTokenConfig.AUDIENCE
import com.joykeepsflowin.util.JwtTokenConfig.ISSUER
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

fun Application.configureSecurity() {
    val logger = LoggerFactory.getLogger("MyAppLogger")

    install(Authentication) {
        jwt("jwt") {
            verifier(
                JWT.require(JwtTokenConfig.algorithm)
                    .withIssuer(ISSUER)
                    .withAudience(AUDIENCE)
                    .withClaimPresence("userId")
                    .build()
            )
            validate { credential ->
                try {
                    val userId = credential.payload.getClaim("userId").asInt()
                    logger.info("userId: $userId")
                    val user = transaction { UserDao.findById(userId) }
                    user?.let {
                        UserIdPrincipal(userId.toString())
                    } ?: throw JWTVerificationException("User not found")
                } catch (e: Exception) {
                    e.printStackTrace()
                    null  // 返回null会触发401 Unauthorized
                }
            }
        }
    }
}