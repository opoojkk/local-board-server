package com.joykeepsflowin.route

import com.joykeepsflowin.auth.makeToken
import com.joykeepsflowin.biz.ResponseShell
import com.joykeepsflowin.biz.user.LoginRequest
import com.joykeepsflowin.biz.user.LoginResponse
import com.joykeepsflowin.biz.user.RegisterRequest
import com.joykeepsflowin.db.dao.UserDao
import com.joykeepsflowin.db.entry.UserTable
import com.joykeepsflowin.util.PasswordHasher
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.authRoutes() {
    post("/register") {
        val request = call.receive<RegisterRequest>()

        // 检查用户名是否已存在
        val existingUser = transaction {
            UserDao.find { UserTable.username eq request.username }.firstOrNull()
        }
        if (existingUser != null) {
            call.respond(ResponseShell<String>(HttpStatusCode.Conflict.value, "Username already exists"))
            return@post
        }
        // 创建用户
        transaction {
            UserDao.new {
                username = request.username
                email = request.email
                passwordHash = PasswordHasher.hash(request.password)
            }
        }
        call.respond(ResponseShell<String>(HttpStatusCode.Created.value, "User registered successfully"))
    }

    post("/login") {
        val request = call.receive<LoginRequest>()
        // 查找用户
        val user = transaction {
            UserDao.find { UserTable.username eq request.username }.firstOrNull()
        }
        // 验证用户和密码
        if (user == null || !PasswordHasher.verify(request.password, user.passwordHash)) {
            call.respond(ResponseShell<String>(HttpStatusCode.Unauthorized.value, "Invalid username or password"))
            return@post
        }
        // 生成 JWT Token（需配置 JWT 认证）
        val token = makeToken(user)
        call.respond(ResponseShell(data = LoginResponse(token)))
    }
}