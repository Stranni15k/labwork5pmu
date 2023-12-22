package ru.ulstu.`is`.pmu.ui.task.edit

import android.content.res.Configuration
import android.widget.CalendarView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.ulstu.`is`.pmu.R
import ru.ulstu.`is`.pmu.database.task.model.User
import ru.ulstu.`is`.pmu.database.task.model.Task
import ru.ulstu.`is`.pmu.common.AppViewModelProvider
import ru.ulstu.`is`.pmu.ui.theme.PmudemoTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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
    var showInvalidDateDialog by remember { mutableStateOf(false) }
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
        var showDatePicker by remember { mutableStateOf(false) }
        if (showDatePicker) {
            DatePicker(
                onDateSelected = { selectedDate ->
                    onUpdate(taskUiState.taskDetails.copy(endDate = SimpleDateFormat("dd.MM.yyyy").format(selectedDate)))
                    showDatePicker = false
                },
                onDismissRequest = {
                    showDatePicker = false
                }
            )
        }

        Button(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Выбрать дату окончания")
        }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = taskUiState.taskDetails.endDate,
            onValueChange = { onUpdate(taskUiState.taskDetails.copy(endDate = it)) },
            label = { Text(stringResource(id = R.string.task_phone)) },
            singleLine = true ,
            enabled = false
        )
        Button(
            onClick = {
                if (!isValidDate(taskUiState.taskDetails.endDate)) {
                    showInvalidDateDialog = true
                } else {
                    onClick()
                }
            },
            enabled = taskUiState.isEntryValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.task_save_button))
        }
    }

    if (showInvalidDateDialog) {
        AlertDialog(
            onDismissRequest = { showInvalidDateDialog = false },
            title = { Text("Неверный формат даты") },
            text = { Text("Введите дату по шаблону: 01.12.2023") },
            confirmButton = {
                Button(onClick = { showInvalidDateDialog = false }) {
                    Text("Подтвердить")
                }
            }
        )
    }
}

fun isValidDate(date: String): Boolean {
    val regex = Regex("""^\d{2}\.\d{2}\.\d{4}$""")
    return regex.matches(date)
}

@Composable
fun CustomCalendarView(onDateSelected: (Date) -> Unit) {
    AndroidView(
        modifier = Modifier.wrapContentSize(),
        factory = { context ->
            // Используем стандартный контекст, без применения кастомной темы
            CalendarView(context).apply {
                // Настройки CalendarView
                val calendar = Calendar.getInstance()

                setOnDateChangeListener { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    onDateSelected(calendar.time)
                }
            }
        }
    )
}

@Composable
fun DatePicker(onDateSelected: (Date) -> Unit, onDismissRequest: () -> Unit) {
    val selDate = remember { mutableStateOf(Date()) }

    //todo - add strings to resource after POC
    Dialog(onDismissRequest = { onDismissRequest() }, properties = DialogProperties()) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .background(
                    color = Color.White
                )
        ) {
            Column(
                Modifier
                    .defaultMinSize(minHeight = 72.dp)
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primary
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = "Выберите дату"
                )

                Spacer(modifier = Modifier.size(24.dp))

                Text(
                    text = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(selDate.value)
                )

                Spacer(modifier = Modifier.size(16.dp))
            }

            CustomCalendarView(onDateSelected = {
                selDate.value = it
            })

            Spacer(modifier = Modifier.size(8.dp))

            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = 16.dp, end = 16.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
            ) {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    //TODO - hardcode string
                    Text(
                        text = "Отмена"
                    )
                }

                TextButton(
                    onClick = {
                        onDateSelected(selDate.value)
                        onDismissRequest()
                    }
                ) {
                    //TODO - hardcode string
                    Text(
                        text = "Подтвердить"
                    )
                }

            }
        }
    }
}