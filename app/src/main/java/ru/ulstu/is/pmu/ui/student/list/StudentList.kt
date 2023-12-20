package ru.ulstu.`is`.pmu.ui.task.list

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.ulstu.`is`.pmu.common.AppViewModelProvider
import ru.ulstu.`is`.pmu.database.task.model.Task
import ru.ulstu.`is`.pmu.ui.navigation.Screen
import ru.ulstu.`is`.pmu.ui.theme.PmudemoTheme

@Composable
fun TaskList(
    navController: NavController,
    viewModel: TaskListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val taskListUiState = viewModel.taskListUiState.collectAsLazyPagingItems()
    Scaffold(
        topBar = {},
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val route = Screen.TaskEdit.route.replace("{id}", 0.toString())
                    navController.navigate(route)
                },
            ) {
                Icon(Icons.Filled.Add, "Добавить")
            }
        }
    ) { innerPadding ->
        TaskList(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            taskList = taskListUiState,
            onClick = { uid: Int ->
                val route = Screen.TaskEdit.route.replace("{id}", uid.toString())
                navController.navigate(route)
            },
            onDeleteClick = { task: Task ->
                // Обработка удаления задачи
                coroutineScope.launch {
                    viewModel.deleteTask(task)
                }
            },
            onAddToFavoritesClick = { task: Task ->
                // Обработка добавления задачи в избранное
                coroutineScope.launch {
                    viewModel.favoriteTask(task)
                }
            },
            onEditClick = { task: Task ->
                // Обработка редактирования задачи
                val route = Screen.TaskEdit.route.replace("{id}", task.uid.toString())
                navController.navigate(route)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissBackground(dismissState: DismissState) {
    val color = when (dismissState.dismissDirection) {
        DismissDirection.StartToEnd -> Color.Transparent
        DismissDirection.EndToStart -> Color(0xFFFF1744)
        null -> Color.Transparent
    }
    val direction = dismissState.dismissDirection

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(12.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        if (direction == DismissDirection.EndToStart) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "delete",
                tint = Color.White
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
private fun SwipeToDelete(
    task: Task,
    onClick: (uid: Int) -> Unit,
    onDeleteClick: (task: Task) -> Unit,
    onAddToFavoritesClick: (task: Task) -> Unit,
    onEditClick: (task: Task) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = String.format("%s %s", task.name, task.description),
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = { onEditClick(task) },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Редактировать",
                    tint = Color.Blue
                )
            }

            IconButton(
                onClick = { onDeleteClick(task) },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = Color.Red
                )
            }

            IconButton(
                onClick = { onAddToFavoritesClick(task) },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Добавить в избранное"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
private fun TaskList(
    modifier: Modifier = Modifier,
    taskList: LazyPagingItems<Task>,
    onClick: (uid: Int) -> Unit,
    onDeleteClick: (task: Task) -> Unit,
    onAddToFavoritesClick: (task: Task) -> Unit,
    onEditClick: (task: Task) -> Unit
) {
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    fun refresh() = refreshScope.launch {
        refreshing = true
        taskList.refresh()
        refreshing = false
    }

    val state = rememberPullRefreshState(refreshing, ::refresh)
    Box(
        modifier = modifier.pullRefresh(state)
    ) {
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            LazyColumn(modifier = Modifier.padding(all = 10.dp)) {
                items(
                    count = taskList.itemCount,
                    key = taskList.itemKey(),
                    contentType = taskList.itemContentType()
                ) { index ->
                    val task = taskList[index]
                    task?.let {
                        SwipeToDelete(
                            task = task,
                            onClick = onClick,
                            onDeleteClick = onDeleteClick,
                            onAddToFavoritesClick = onAddToFavoritesClick,
                            onEditClick = onEditClick
                        )
                    }
                }
            }
            PullRefreshIndicator(
                refreshing, state,
                Modifier
                    .align(CenterHorizontally)
                    .zIndex(100f)
            )
        }
    }
}



@Composable
private fun TaskListItem(
    task: Task, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = modifier.padding(all = 10.dp)
        ) {
            Text(
                text = String.format("%s %s", task.name, task.description)
            )
        }
    }
}