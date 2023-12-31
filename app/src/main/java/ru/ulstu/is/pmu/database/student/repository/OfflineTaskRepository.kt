package ru.ulstu.`is`.pmu.database.task.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ru.ulstu.`is`.pmu.common.AppContainer
import ru.ulstu.`is`.pmu.common.TaskRepository
import ru.ulstu.`is`.pmu.database.task.dao.TaskDao
import ru.ulstu.`is`.pmu.database.task.model.Task

class OfflineTaskRepository(private val taskDao: TaskDao) : TaskRepository {
    override fun getAllTasks(): Flow<PagingData<Task>> = Pager(
        config = PagingConfig(
            pageSize = AppContainer.LIMIT,
            enablePlaceholders = false
        ),
        pagingSourceFactory = taskDao::getAll
    ).flow

    override fun getAllFavoriteTasks(): Flow<PagingData<Task>> = Pager(
        config = PagingConfig(
            pageSize = AppContainer.LIMIT,
            enablePlaceholders = false
        ),
        pagingSourceFactory = taskDao::getFavoriteTasks
    ).flow

    override fun getAllDateTasks(): Flow<PagingData<Task>> = Pager(
        config = PagingConfig(
            pageSize = AppContainer.LIMIT,
            enablePlaceholders = false
        ),
        pagingSourceFactory = taskDao::getTasksSortedByDate
    ).flow

    override suspend fun getTask(uid: Int): Task = taskDao.getByUid(uid).first()

    override suspend fun insertTask(task: Task) = taskDao.insert(task)

    override suspend fun updateTask(task: Task) = taskDao.update(task)

    override suspend fun deleteTask(task: Task) = taskDao.delete(task)

    override suspend fun favoriteTask(task: Task) {
        taskDao.updateFavorite(task.uid, !task.favorite)
    }

    fun getAllTasksPagingSource(): PagingSource<Int, Task> = taskDao.getAll()

    fun getAllTasksFavoritePagingSource(): PagingSource<Int, Task> = taskDao.getFavoriteTasks()

    fun getAllTasksDatePagingSource(): PagingSource<Int, Task> = taskDao.getTasksSortedByDate()

    suspend fun insertTasks(tasks: List<Task>) =
        taskDao.insert(*tasks.toTypedArray())

    suspend fun clearTasks() = taskDao.deleteAll()
}