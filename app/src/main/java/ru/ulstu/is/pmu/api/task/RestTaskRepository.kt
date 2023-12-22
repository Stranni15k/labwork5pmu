package ru.ulstu.`is`.pmu.api.task

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.ulstu.`is`.pmu.api.MyServerService
import ru.ulstu.`is`.pmu.api.user.RestUserRepository
import ru.ulstu.`is`.pmu.api.model.toTask
import ru.ulstu.`is`.pmu.api.model.toTaskRemote
import ru.ulstu.`is`.pmu.common.AppContainer
import ru.ulstu.`is`.pmu.common.TaskRepository
import ru.ulstu.`is`.pmu.database.AppDatabase
import ru.ulstu.`is`.pmu.database.remotekeys.repository.OfflineRemoteKeyRepository
import ru.ulstu.`is`.pmu.database.task.model.Task
import ru.ulstu.`is`.pmu.database.task.repository.OfflineTaskRepository

class RestTaskRepository(
    private val service: MyServerService,
    private val dbTaskRepository: OfflineTaskRepository,
    private val dbRemoteKeyRepository: OfflineRemoteKeyRepository,
    private val userRestRepository: RestUserRepository,
    private val database: AppDatabase
) : TaskRepository {
    override fun getAllTasks(): Flow<PagingData<Task>> {
        Log.d(RestTaskRepository::class.simpleName, "Get tasks")

        val pagingSourceFactory = { dbTaskRepository.getAllTasksPagingSource() }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = AppContainer.LIMIT,
                enablePlaceholders = false
            ),
            remoteMediator = TaskRemoteMediator(
                service,
                dbTaskRepository,
                dbRemoteKeyRepository,
                userRestRepository,
                database,
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override fun getAllFavoriteTasks(): Flow<PagingData<Task>> {
        Log.d(RestTaskRepository::class.simpleName, "Get tasks")

        val pagingSourceFactory = { dbTaskRepository.getAllTasksFavoritePagingSource() }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = AppContainer.LIMIT,
                enablePlaceholders = false
            ),
            remoteMediator = TaskRemoteMediator(
                service,
                dbTaskRepository,
                dbRemoteKeyRepository,
                userRestRepository,
                database,
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override fun getAllDateTasks(): Flow<PagingData<Task>> {
        Log.d(RestTaskRepository::class.simpleName, "Get tasks")

        val pagingSourceFactory = { dbTaskRepository.getAllTasksDatePagingSource() }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = AppContainer.LIMIT,
                enablePlaceholders = false
            ),
            remoteMediator = TaskRemoteMediator(
                service,
                dbTaskRepository,
                dbRemoteKeyRepository,
                userRestRepository,
                database,
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override suspend fun getTask(uid: Int): Task =
        service.getTask(uid).toTask()

    override suspend fun insertTask(task: Task) {
        service.createTask(task.toTaskRemote()).toTask()
    }

    override suspend fun updateTask(task: Task) {
        service.updateTask(task.uid, task.toTaskRemote()).toTask()
    }

    override suspend fun deleteTask(task: Task) {
        service.deleteTask(task.uid).toTask()
    }

    override suspend fun favoriteTask(task: Task) {
        task.favorite = true
        service.updateTask(task.uid, task.toTaskRemote()).toTask()
    }
}