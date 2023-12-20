package ru.ulstu.`is`.pmu.common

import android.content.Context
import ru.ulstu.`is`.pmu.api.MyServerService
import ru.ulstu.`is`.pmu.api.user.RestUserRepository
import ru.ulstu.`is`.pmu.api.task.RestTaskRepository
import ru.ulstu.`is`.pmu.database.AppDatabase
import ru.ulstu.`is`.pmu.database.remotekeys.repository.OfflineRemoteKeyRepository
import ru.ulstu.`is`.pmu.database.task.repository.OfflineUserRepository
import ru.ulstu.`is`.pmu.database.task.repository.OfflineTaskRepository

interface AppContainer {
    val taskRestRepository: RestTaskRepository
    val userRestRepository: RestUserRepository

    companion object {
        const val TIMEOUT = 5000L
        const val LIMIT = 10
    }
}

class AppDataContainer(private val context: Context) : AppContainer {
    private val taskRepository: OfflineTaskRepository by lazy {
        OfflineTaskRepository(AppDatabase.getInstance(context).taskDao())
    }
    private val userRepository: OfflineUserRepository by lazy {
        OfflineUserRepository(AppDatabase.getInstance(context).userDao())
    }
    private val remoteKeyRepository: OfflineRemoteKeyRepository by lazy {
        OfflineRemoteKeyRepository(AppDatabase.getInstance(context).remoteKeysDao())
    }
    override val taskRestRepository: RestTaskRepository by lazy {
        RestTaskRepository(
            MyServerService.getInstance(),
            taskRepository,
            remoteKeyRepository,
            userRestRepository,
            AppDatabase.getInstance(context)
        )
    }
    override val userRestRepository: RestUserRepository by lazy {
        RestUserRepository(
            MyServerService.getInstance(),
            userRepository
        )
    }
}