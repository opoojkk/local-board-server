package com.joykeepsflowin.module

import com.joykeepsflowin.route.authRoutes
import com.joykeepsflowin.route.bulletinRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        bulletinRoutes()
        authRoutes()
    }
}
