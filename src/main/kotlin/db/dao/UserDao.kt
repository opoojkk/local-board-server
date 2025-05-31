package com.joykeepsflowin.db.dao

import com.joykeepsflowin.db.entry.UserTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserDao>(UserTable)

    var username by UserTable.username
    var email by UserTable.email
    var passwordHash by UserTable.passwordHash
    var createdAt by UserTable.createdAt
    var updatedAt by UserTable.updatedAt
    var isActive by UserTable.isActive
}
