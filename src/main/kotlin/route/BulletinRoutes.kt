package com.joykeepsflowin.route

import com.joykeepsflowin.biz.ResponseShell
import com.joykeepsflowin.biz.bulletin.BulletinDTO
import com.joykeepsflowin.biz.bulletin.BulletinPaginationResponse
import com.joykeepsflowin.biz.bulletin.CreateBulletinRequest
import com.joykeepsflowin.biz.bulletin.ForbiddenException
import com.joykeepsflowin.db.dao.UserDao
import com.joykeepsflowin.db.entry.Bulletins
import com.joykeepsflowin.db.entry.UserTable
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.bulletinRoutes() {
    // GET /bulletins?page=1&size=10&pinnedFirst=true
    route("/bulletins/list") {
        authenticate("jwt") {
            get {
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10
                val pinnedFirst = call.request.queryParameters["pinnedFirst"]?.toBoolean() ?: true
                val bulletins = transaction {
                    // 置顶公告优先 + 分页逻辑
                    Bulletins.selectAll()
                        .orderBy(if (pinnedFirst) Bulletins.isPinned to SortOrder.DESC else Bulletins.id to SortOrder.DESC)
                        .limit(size, offset = ((page - 1) * size).toLong())
                        .map { row ->
                            BulletinDTO.fromRow(
                                id = row[Bulletins.id],
                                title = row[Bulletins.title],
                                content = row[Bulletins.content],
                                isPinned = row[Bulletins.isPinned],
                                createdAt = row[Bulletins.createdAt],
                                updatedAt = row[Bulletins.updatedAt],
                                createdBy = row[Bulletins.createdBy]
                            )
                        }
                }

                call.respond(
                    ResponseShell(
                        data = BulletinPaginationResponse(
                            data = bulletins,
                            total = bulletins.size,
                            page = page,
                            pageSize = size
                        )
                    )

                )
            }
        }
    }


    // POST /bulletins
    route("/bulletins/add") {
        authenticate("jwt") {
            post {
                val request = call.receive<CreateBulletinRequest>()
                val userId = call.principal<com.joykeepsflowin.auth.UserIdPrincipal>()?.userId // 从认证中获取用户ID
                if (userId == null) {
                    call.respond(ResponseShell<String>(HttpStatusCode.Forbidden.value, "Wrong user id"))
                    return@post
                }
                val user = transaction {
                    UserDao.find { UserTable.id eq userId.toInt() }.firstOrNull()
                }
                if (user == null) {
                    call.respond(ResponseShell<String>(code = HttpStatusCode.Forbidden.value, msg = "User not found"))
                    return@post
                }
                val id = transaction {
                    Bulletins.insert {
                        it[title] = request.title
                        it[content] = request.content // 存储原始Markdown
                        it[isPinned] = request.isPinned
                        it[createdBy] = userId // 记录创建人
                    } get Bulletins.id
                }
                call.respond(ResponseShell<String>(msg = "Successfully created a bulletin"))
            }
        }
    }

    // GET /bulletins/{id}
    route("/bulletins/{id}") {
        authenticate("jwt") {
            get {
                val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                val bulletin = transaction {
                    Bulletins.select { Bulletins.id eq id }
                        .singleOrNull()?.let { row ->
                            BulletinDTO.fromRow(
                                id = row[Bulletins.id],
                                title = row[Bulletins.title],
                                content = row[Bulletins.content],
                                isPinned = row[Bulletins.isPinned],
                                createdAt = row[Bulletins.createdAt],
                                updatedAt = row[Bulletins.updatedAt],
                                createdBy = row[Bulletins.createdBy]
                            )
                        }
                }
                if (bulletin == null) {
                    throw NotFoundException("Bulletin not found")
                }
                call.respond(ResponseShell(data = bulletin))
            }
        }
    }

    // DELETE /bulletins/123
    route("/bulletins/{id}") {
        authenticate("jwt") {
            delete {
                val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                val userId = call.principal<com.joykeepsflowin.auth.UserIdPrincipal>()?.userId

                transaction {
                    // 权限验证
                    val createdBy = Bulletins.slice(Bulletins.createdBy)
                        .select { Bulletins.id eq id }
                        .singleOrNull()?.get(Bulletins.createdBy)
                        ?: throw NotFoundException()

                    if (createdBy != userId) {
                        throw ForbiddenException("No permission to delete")
                    }

                    Bulletins.deleteWhere { Bulletins.id eq id }
                }
                call.respond(ResponseShell<String>(HttpStatusCode.NoContent.value))
            }
        }
    }

    // POST /bulletins/pin/{id}
    route("/bulletins/pin/{id}") {
        authenticate("jwt") {
            post {
                val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                val userId = call.principal<com.joykeepsflowin.auth.UserIdPrincipal>()?.userId

                transaction {
                    // 权限验证
                    val createdBy = Bulletins.slice(Bulletins.createdBy)
                        .select { Bulletins.id eq id }
                        .singleOrNull()?.get(Bulletins.createdBy)
                        ?: throw NotFoundException()

                    if (createdBy != userId) {
                        throw ForbiddenException("No permission to pin")
                    }

                    Bulletins.update({ Bulletins.id eq id }) {
                        it[isPinned] = true
                    }
                }
                call.respond(ResponseShell<String>(HttpStatusCode.NoContent.value))
            }
        }
    }

    // POST /bulletins/unpin/{id}
    route("/bulletins/unpin/{id}") {
        authenticate("jwt") {
            post {
                val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                val userId = call.principal<com.joykeepsflowin.auth.UserIdPrincipal>()?.userId

                transaction {
                    // 权限验证
                    val createdBy = Bulletins.slice(Bulletins.createdBy)
                        .select { Bulletins.id eq id }
                        .singleOrNull()?.get(Bulletins.createdBy)
                        ?: throw NotFoundException()

                    if (createdBy != userId) {
                        throw ForbiddenException("No permission to unpin")
                    }

                    Bulletins.update({ Bulletins.id eq id }) {
                        it[isPinned] = false
                    }
                }
                call.respond(ResponseShell<String>(HttpStatusCode.NoContent.value))
            }
        }
    }

    // POST /bulletins/update/{id}
    route("/bulletins/update/{id}") {
        authenticate("jwt") {
            post {
                val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                val request = call.receive<CreateBulletinRequest>()
                val userId = call.principal<com.joykeepsflowin.auth.UserIdPrincipal>()?.userId

                transaction {
                    // 权限验证
                    val createdBy = Bulletins.slice(Bulletins.createdBy)
                        .select { Bulletins.id eq id }
                        .singleOrNull()?.get(Bulletins.createdBy)
                        ?: throw NotFoundException()

                    if (createdBy != userId) {
                        throw ForbiddenException("No permission to update")
                    }

                    Bulletins.update({ Bulletins.id eq id }) {
                        it[title] = request.title
                        it[content] = request.content
                        it[isPinned] = request.isPinned
                    }
                }
                call.respond(ResponseShell<String>(msg = "Successfully updated the bulletin"))
            }
        }
    }
}