package ru.ulstu.`is`.pmu.common

import ru.ulstu.`is`.pmu.database.task.model.User

interface UserRepository {
    suspend fun getAllUsers(): List<User>
}