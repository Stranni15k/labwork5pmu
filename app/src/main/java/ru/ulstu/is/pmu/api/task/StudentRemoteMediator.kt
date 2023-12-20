package ru.ulstu.`is`.pmu.api.task

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.HttpException
import ru.ulstu.`is`.pmu.api.MyServerService
import ru.ulstu.`is`.pmu.api.user.RestUserRepository
import ru.ulstu.`is`.pmu.api.model.toTask
import ru.ulstu.`is`.pmu.database.AppDatabase
import ru.ulstu.`is`.pmu.database.remotekeys.model.RemoteKeyType
import ru.ulstu.`is`.pmu.database.remotekeys.model.RemoteKeys
import ru.ulstu.`is`.pmu.database.remotekeys.repository.OfflineRemoteKeyRepository
import ru.ulstu.`is`.pmu.database.task.model.Task
import ru.ulstu.`is`.pmu.database.task.repository.OfflineTaskRepository
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class TaskRemoteMediator(
    private val service: MyServerService,
    private val dbTaskRepository: OfflineTaskRepository,
    private val dbRemoteKeyRepository: OfflineRemoteKeyRepository,
    private val userRestRepository: RestUserRepository,
    private val database: AppDatabase
) : RemoteMediator<Int, Task>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Task>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: 1
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
        }

        try {
            val tasks = service.getTasks(page, state.config.pageSize).map { it.toTask() }
            val endOfPaginationReached = tasks.isEmpty()
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    dbRemoteKeyRepository.deleteRemoteKey(RemoteKeyType.STUDENT)
                    dbTaskRepository.clearTasks()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = tasks.map {
                    RemoteKeys(
                        entityId = it.uid,
                        type = RemoteKeyType.STUDENT,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }
                userRestRepository.getAllUsers()
                dbRemoteKeyRepository.createRemoteKeys(keys)
                dbTaskRepository.insertTasks(tasks)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Task>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { task ->
                dbRemoteKeyRepository.getAllRemoteKeys(task.uid, RemoteKeyType.STUDENT)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Task>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { task ->
                dbRemoteKeyRepository.getAllRemoteKeys(task.uid, RemoteKeyType.STUDENT)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Task>
    ): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.uid?.let { taskUid ->
                dbRemoteKeyRepository.getAllRemoteKeys(taskUid, RemoteKeyType.STUDENT)
            }
        }
    }

}