package ru.ulstu.`is`.pmu.api.user

import android.util.Log
import ru.ulstu.`is`.pmu.api.MyServerService
import ru.ulstu.`is`.pmu.api.task.RestTaskRepository
import ru.ulstu.`is`.pmu.database.task.model.User
import ru.ulstu.`is`.pmu.common.UserRepository
import ru.ulstu.`is`.pmu.database.task.repository.OfflineUserRepository
import ru.ulstu.`is`.pmu.api.model.toUser

class RestUserRepository(
    private val service: MyServerService,
    private val dbUserRepository: OfflineUserRepository,
): UserRepository {
    override suspend fun getAllUsers(): List<User> {
        Log.d(RestTaskRepository::class.simpleName, "Get users")

        val existUsers = dbUserRepository.getAllUsers().associateBy { it.uid }.toMutableMap()

        service.getUsers()
            .map { it.toUser() }
            .forEach { user ->
            val existUser = existUsers[user.uid]
            if (existUser == null) {
                dbUserRepository.createUser(user)
            } else if (existUser != user) {
                dbUserRepository.createUser(user)
            }
            existUsers[user.uid] = user
        }

        return existUsers.map { it.value }.sortedBy { it.uid }
    }
}