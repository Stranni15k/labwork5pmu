package ru.ulstu.`is`.pmu.common

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ru.ulstu.`is`.pmu.TaskApplication
import ru.ulstu.`is`.pmu.ui.task.edit.UserDropDownViewModel
import ru.ulstu.`is`.pmu.ui.task.edit.TaskEditViewModel
import ru.ulstu.`is`.pmu.ui.task.list.FavoriteTaskList
import ru.ulstu.`is`.pmu.ui.task.list.FavoriteTaskListViewModel
import ru.ulstu.`is`.pmu.ui.task.list.TaskListViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            TaskListViewModel(taskApplication().container.taskRestRepository)
        }
        initializer {
            TaskEditViewModel(
                this.createSavedStateHandle(),
                taskApplication().container.taskRestRepository
            )
        }
        initializer {
            UserDropDownViewModel(taskApplication().container.userRestRepository)
        }
        initializer {
            FavoriteTaskListViewModel(taskApplication().container.taskRestRepository)
        }
    }
}

fun CreationExtras.taskApplication(): TaskApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TaskApplication)