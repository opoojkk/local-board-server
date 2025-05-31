package com.joykeepsflowin.module

import com.auth0.jwt.JWT
import com.auth0.jwt.RegisteredClaims.ISSUER
import com.joykeepsflowin.db.dao.UserDao
import com.joykeepsflowin.util.JwtTokenHelper.algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {

    install(Authentication) {
        jwt {
            verifier(
                JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
            )
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asInt()
                UserDao.findById(userId)?.let { JWTPrincipal(credential.payload) }
            }
        }
    }
}