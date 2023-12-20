package ru.ulstu.`is`.pmu.database.task.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ru.ulstu.`is`.pmu.database.task.model.User

@Dao
interface UserDao {
    @Query("select * from users order by login collate nocase asc")
    suspend fun getAll(): List<User>

    @Insert
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)
}