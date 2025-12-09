package saikrishnas3495275.pollingapp.madproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import saikrishnas3495275.pollingapp.madproject.teacher.ProfileScreen
import saikrishnas3495275.pollingapp.madproject.teacher.CreatePollScreen
import saikrishnas3495275.pollingapp.madproject.teacher.ManagePollsScreen
import saikrishnas3495275.pollingapp.madproject.teacher.PollDetailsScreen
import saikrishnas3495275.pollingapp.madproject.ui.theme.FormPollingAppTheme

sealed class Screen(val route: String, val icon: ImageVector? = null, val title: String) {
    object CreatePoll : Screen("create_poll", Icons.Default.Add, "Create Poll")
    object ManagePolls : Screen("manage_polls", Icons.Default.List, "Manage Polls")
    object PollDetails : Screen("pollDetails/{pollId}", title = "Poll Details")
    object Profile : Screen("teacher_profile", title = "Profile")
}


class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FormPollingAppTheme {
                TeacherDashboard()
            }
        }
    }
}

@Composable
fun TeacherDashboard() {
    val navController = rememberNavController()

    val context = LocalContext.current

    val items = listOf(
        Screen.CreatePoll,
        Screen.ManagePolls,
    )
    Scaffold(
        bottomBar = {
            BottomAppBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon!!, contentDescription = null) },
                        label = { Text(screen.title) },
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
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.CreatePoll.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Screen.CreatePoll.route) { CreatePollScreen(navController) }
            composable(Screen.ManagePolls.route) { ManagePollsScreen(navController) }
            composable(Screen.PollDetails.route) { backStackEntry ->
                val pollId = backStackEntry.arguments?.getString("pollId")
                if (pollId != null) {
                    PollDetailsScreen(pollId = pollId, navController = navController)
                }
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    user = UserData(
                        UserPrefs.getName(context),
                        UserPrefs.getRole(context),
                        UserPrefs.getEmail(context),
                        UserPrefs.getPass(context)
                    )
                    ,navController
                )
            }

        }
    }
}