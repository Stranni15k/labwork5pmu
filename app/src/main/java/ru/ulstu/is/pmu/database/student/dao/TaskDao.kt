package ru.ulstu.`is`.pmu.database.task.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.ulstu.`is`.pmu.database.task.model.Task

@Dao
interface TaskDao {
    @Query("select * from tasks order by name collate nocase asc")
    fun getAll(): PagingSource<Int, Task>

    @Query("SELECT * FROM tasks WHERE favorite = 1")
    fun getFavoriteTasks(): PagingSource<Int, Task>

    @Query("SELECT * FROM tasks ORDER BY DATE(SUBSTR(endDate, 7, 4) || '-' || SUBSTR(endDate, 4, 2) || '-' || SUBSTR(endDate, 1, 2)) ASC")
    fun getTasksSortedByDate(): PagingSource<Int, Task>

    @Query("select * from tasks where tasks.uid = :uid")
    fun getByUid(uid: Int): Flow<Task>

    @Insert
    suspend fun insert(vararg task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("UPDATE tasks SET favorite = :favorite WHERE uid = :taskId")
    suspend fun updateFavorite(taskId: Int, favorite: Boolean)

    @Query("DELETE FROM tasks")
    suspend fun deleteAll()
}