package ru.ulstu.`is`.pmu.ui.task.edit

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.ulstu.`is`.pmu.R
import ru.ulstu.`is`.pmu.database.task.model.User
import ru.ulstu.`is`.pmu.database.task.model.Task
import ru.ulstu.`is`.pmu.common.AppViewModelProvider
import ru.ulstu.`is`.pmu.ui.theme.PmudemoTheme

@Composable
fun TaskEdit(
    navController: NavController,
    viewModel: TaskEditViewModel = viewModel(factory = AppViewModelProvider.Factory),
    userViewModel: UserDropDownViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    userViewModel.setCurrentUser(viewModel.taskUiState.taskDetails.userId)
    TaskEdit(
        taskUiState = viewModel.taskUiState,
        userUiState = userViewModel.userUiState,
        usersListUiState = userViewModel.usersListUiState,
        onClick = {
            coroutineScope.launch {
                viewModel.saveTask()
                navController.popBackStack()
            }
        },
        onUpdate = viewModel::updateUiState,
        onUserUpdate = userViewModel::updateUiState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserDropDown(
    userUiState: UserUiState,
    usersListUiState: UsersListUiState,
    onUserUpdate: (User) -> Unit
) {
    var expanded: Boolean by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        modifier = Modifier
            .padding(top = 7.dp),
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            value = userUiState.user?.name
                ?: stringResource(id = R.string.task_user_not_select),
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .exposedDropdownSize()
        ) {
            usersListUiState.userList.forEach { user ->
                DropdownMenuItem(
                    text = {
                        Text(text = user.name)
                    },
                    onClick = {
                        onUserUpdate(user)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskEdit(
    taskUiState: TaskUiState,
    userUiState: UserUiState,
    usersListUiState: UsersListUiState,
    onClick: () -> Unit,
    onUpdate: (TaskDetails) -> Unit,
    onUserUpdate: (User) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(all = 10.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = taskUiState.taskDetails.name,
            onValueChange = { onUpdate(taskUiState.taskDetails.copy(name = it)) },
            label = { Text(stringResource(id = R.string.task_firstname)) },
            singleLine = true
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = taskUiState.taskDetails.description,
            onValueChange = { onUpdate(taskUiState.taskDetails.copy(description = it)) },
            label = { Text(stringResource(id = R.string.task_lastname)) },
            singleLine = true
        )
        UserDropDown(
            userUiState = userUiState,
            usersListUiState = usersListUiState,
            onUserUpdate = {
                onUpdate(taskUiState.taskDetails.copy(userId = it.uid))
                onUserUpdate(it)
            }
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = taskUiState.taskDetails.endDate,
            onValueChange = { onUpdate(taskUiState.taskDetails.copy(endDate = it)) },
            label = { Text(stringResource(id = R.string.task_phone)) },
            singleLine = true
        )
        Button(
            onClick = onClick,
            enabled = taskUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.task_save_button))
        }
    }
}

@Preview(name = "Light Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TaskEditPreview() {
    PmudemoTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            TaskEdit(
                taskUiState = Task.getTask().toUiState(true),
                userUiState = User.DEMO_User.toUiState(),
                usersListUiState = UsersListUiState(listOf()),
                onClick = {},
                onUpdate = {},
                onUserUpdate = {}
            )
        }
    }
}