package ru.ulstu.`is`.pmu.database.task.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow
import ru.ulstu.`is`.pmu.common.AppContainer
import ru.ulstu.`is`.pmu.common.UserRepository
import ru.ulstu.`is`.pmu.database.task.dao.UserDao
import ru.ulstu.`is`.pmu.database.task.model.Task
import ru.ulstu.`is`.pmu.database.task.model.User

class OfflineUserRepository(private val userDao: UserDao) : UserRepository {
    override suspend fun getAllUsers(): List<User> = userDao.getAll()

    suspend fun insertUsers(users: List<User>) =
        userDao.insert(*users.toTypedArray())

    suspend fun createUser(user: User) = userDao.insert(user)
    suspend fun updateUser(user: User) = userDao.update(user)
    suspend fun clearUsers() = userDao.deleteAll()
}