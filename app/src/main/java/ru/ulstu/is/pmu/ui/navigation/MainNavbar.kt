package ru.ulstu.`is`.pmu.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.ulstu.`is`.pmu.R
import ru.ulstu.`is`.pmu.ui.about.About
import ru.ulstu.`is`.pmu.ui.task.edit.TaskEdit
import ru.ulstu.`is`.pmu.ui.task.list.FavoriteTaskList
import ru.ulstu.`is`.pmu.ui.task.list.TaskList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Topbar(
    navController: NavHostController,
    currentScreen: Screen?
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        title = {
            Text(stringResource(currentScreen?.resourceId ?: R.string.app_name))
        },
        navigationIcon = {
            if (
                navController.previousBackStackEntry != null
                && (currentScreen == null || !currentScreen.showInBottomBar)
            ) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    )
}

@Composable
fun Navbar(
    navController: NavHostController,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier) {
        Screen.bottomBarItems.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(stringResource(screen.resourceId)) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun Navhost(
    navController: NavHostController,
    innerPadding: PaddingValues, modifier:
    Modifier = Modifier
) {
    NavHost(
        navController,
        startDestination = Screen.TaskList.route,
        modifier.padding(innerPadding)
    ) {
        composable(Screen.TaskList.route) { TaskList(navController) }
        composable(Screen.About.route) { About() }
        composable(
            Screen.TaskEdit.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            TaskEdit(navController)
        }
        composable(Screen.TaskFavoriteList.route) { FavoriteTaskList(navController) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavbar() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentScreen = currentDestination?.route?.let { Screen.getItem(it) }

    Scaffold(
        topBar = {
            Topbar(navController, currentScreen)
        },
        bottomBar = {
            if (currentScreen == null || currentScreen.showInBottomBar) {
                Navbar(navController, currentDestination)
            }
        }
    ) { innerPadding ->
        Navhost(navController, innerPadding)
    }
}