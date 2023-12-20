package ru.ulstu.`is`.pmu.api.model

import kotlinx.serialization.Serializable
import ru.ulstu.`is`.pmu.database.task.model.User

@Serializable
data class UserRemote(
    val id: Int = 0,
    val name: String,
    val login: String
)

fun UserRemote.toUser(): User = User(
    id,
    name,
    login
)
