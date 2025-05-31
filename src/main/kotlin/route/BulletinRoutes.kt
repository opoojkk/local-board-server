package com.joykeepsflowin.route

import com.joykeepsflowin.biz.bulletin.BulletinDTO
import com.joykeepsflowin.biz.bulletin.BulletinPaginationResponse
import com.joykeepsflowin.biz.bulletin.CreateBulletinRequest
import com.joykeepsflowin.biz.bulletin.ForbiddenException
import com.joykeepsflowin.db.entry.Bulletins
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
    get("/bulletins/list") {
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
            BulletinPaginationResponse(
                data = bulletins,
                total = bulletins.size,
                page = page,
                pageSize = size
            )
        )
    }

    // POST /bulletins
    post("/bulletins/add") {
        val request = call.receive<CreateBulletinRequest>()
        val userId = call.principal<com.joykeepsflowin.auth.UserIdPrincipal>()?.id // 从认证中获取用户ID

        val id = transaction {
            Bulletins.insert {
                it[title] = request.title
                it[content] = request.content // 存储原始Markdown
                it[isPinned] = request.isPinned
                it[createdBy] = userId // 记录创建人
            } get Bulletins.id
        }
        call.respond(mapOf("id" to id))
    }

    // DELETE /bulletins/123
    delete("/bulletins/{id}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
        val userId = call.principal<com.joykeepsflowin.auth.UserIdPrincipal>()?.id

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
        call.respond(HttpStatusCode.NoContent)
    }
}