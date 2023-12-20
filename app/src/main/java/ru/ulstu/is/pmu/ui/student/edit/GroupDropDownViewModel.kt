package ru.ulstu.`is`.pmu.ui.task.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.ulstu.`is`.pmu.common.UserRepository
import ru.ulstu.`is`.pmu.database.task.model.User

class UserDropDownViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    var usersListUiState by mutableStateOf(UsersListUiState())
        private set

    var userUiState by mutableStateOf(UserUiState())
        private set

    init {
        viewModelScope.launch {
            usersListUiState = UsersListUiState(userRepository.getAllUsers())
        }
    }

    fun setCurrentUser(userId: Int) {
        val user: User? =
            usersListUiState.userList.firstOrNull { user -> user.uid == userId }
        user?.let { updateUiState(it) }
    }

    fun updateUiState(user: User) {
        userUiState = UserUiState(
            user = user
        )
    }
}

data class UsersListUiState(val userList: List<User> = listOf())

data class UserUiState(
    val user: User? = null
)

fun User.toUiState() = UserUiState(user = User(uid = uid, name = name, login = login))