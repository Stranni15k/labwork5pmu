package ru.ulstu.`is`.pmu.api.model

import kotlinx.serialization.Serializable
import ru.ulstu.`is`.pmu.database.task.model.Task

@Serializable
data class TaskRemote(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val endDate: String = "",
    val favorite: Boolean,
    val userId: Int,
)

fun TaskRemote.toTask(): Task = Task(
    id,
    name,
    description,
    endDate,
    favorite = false,
    userId
)

fun Task.toTaskRemote(): TaskRemote = TaskRemote(
    uid,
    name,
    description,
    endDate,
    favorite = false,
    userId = 1
)