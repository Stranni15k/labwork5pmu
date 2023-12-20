package ru.ulstu.`is`.pmu.common

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.ulstu.`is`.pmu.database.task.model.Task

interface TaskRepository {
    fun getAllTasks(): Flow<PagingData<Task>>
    suspend fun getTask(uid: Int): Task
    suspend fun insertTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
}