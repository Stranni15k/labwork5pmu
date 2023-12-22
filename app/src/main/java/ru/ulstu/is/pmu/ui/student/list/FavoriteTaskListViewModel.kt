package ru.ulstu.`is`.pmu.ui.task.list

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.ulstu.`is`.pmu.common.TaskRepository
import ru.ulstu.`is`.pmu.database.task.model.Task

class FavoriteTaskListViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    val taskListUiState: Flow<PagingData<Task>> = taskRepository.getAllFavoriteTasks()
}