package ru.ulstu.`is`.pmu.ui.task.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.ulstu.`is`.pmu.common.TaskRepository
import ru.ulstu.`is`.pmu.database.task.model.Task

class TaskEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository
) : ViewModel() {

    var taskUiState by mutableStateOf(TaskUiState())
        private set

    private val taskUid: Int = checkNotNull(savedStateHandle["id"])

    init {
        viewModelScope.launch {
            if (taskUid > 0) {
                taskUiState = taskRepository.getTask(taskUid)
                    .toUiState(true)
            }
        }
    }

    fun updateUiState(taskDetails: TaskDetails) {
        taskUiState = TaskUiState(
            taskDetails = taskDetails,
            isEntryValid = validateInput(taskDetails)
        )
    }

    suspend fun saveTask() {
        if (validateInput()) {
            if (taskUid > 0) {
                taskRepository.updateTask(
                    taskUiState.taskDetails.toTask(taskUid)
                )
            } else {
                taskRepository.insertTask(
                    taskUiState.taskDetails.toTask()
                )
            }
        }
    }

    private fun validateInput(uiState: TaskDetails = taskUiState.taskDetails): Boolean {
        return with(uiState) {
            name.isNotBlank()
                    && description.isNotBlank()
                    && endDate.isNotBlank()
                    && userId > 0
        }
    }
}

data class TaskUiState(
    val taskDetails: TaskDetails = TaskDetails(),
    val isEntryValid: Boolean = false
)

data class TaskDetails(
    val name: String = "",
    val description: String = "",
    val endDate: String = "",
    val favorite: Boolean = false,
    val userId: Int = 0
)

fun TaskDetails.toTask(uid: Int = 0): Task = Task(
    uid = uid,
    name = name,
    description = description,
    endDate = endDate,
    favorite = favorite,
    userId = userId
)

fun Task.toDetails(): TaskDetails = TaskDetails(
    name = name,
    description = description,
    endDate = endDate,
    favorite = favorite,
    userId = userId
)

fun Task.toUiState(isEntryValid: Boolean = false): TaskUiState = TaskUiState(
    taskDetails = this.toDetails(),
    isEntryValid = isEntryValid
)