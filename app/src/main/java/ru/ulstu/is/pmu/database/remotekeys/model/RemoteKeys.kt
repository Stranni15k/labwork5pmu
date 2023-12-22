package ru.ulstu.`is`.pmu.database.remotekeys.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import ru.ulstu.`is`.pmu.database.task.model.Task
import ru.ulstu.`is`.pmu.database.task.model.User

enum class RemoteKeyType(private val type: String) {
    STUDENT(Task::class.simpleName ?: "Task"),
    USER(User::class.simpleName ?: "User");

    @TypeConverter
    fun toRemoteKeyType(value: String) = RemoteKeyType.values().first { it.type == value }

    @TypeConverter
    fun fromRemoteKeyType(value: RemoteKeyType) = value.type
}

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val entityId: Int,
    @TypeConverters(RemoteKeyType::class)
    val type: RemoteKeyType,
    val prevKey: Int?,
    val nextKey: Int?
)
