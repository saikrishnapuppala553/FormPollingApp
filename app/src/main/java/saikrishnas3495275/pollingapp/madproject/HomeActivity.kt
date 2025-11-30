package saikrishnas3495275.pollingapp.madproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import saikrishnas3495275.pollingapp.madproject.teacher.CreatePollScreen
import saikrishnas3495275.pollingapp.madproject.teacher.ManagePoll
import saikrishnas3495275.pollingapp.madproject.teacher.PollDetailsScreen
import saikrishnas3495275.pollingapp.madproject.teacher.generateDummyPolls

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreen()
        }
    }
}



@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}

@Composable
fun HomeScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavigationGraph(navController)
        }
    }
}

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object AddPollV : BottomNavItem("addpoll", "Add Poll", Icons.Default.Add)
    object MPollV : BottomNavItem("mpoll", "Manage Poll", Icons.Default.ThumbUp)
    object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
}




@Composable
fun ProfileScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Profile Screen")
    }
}


@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.AddPollV.route
    ) {
        composable(BottomNavItem.AddPollV.route) { CreatePollScreen() }
        composable(BottomNavItem.MPollV.route) { ManagePoll(navController = navController) }
        composable(BottomNavItem.Profile.route) { ProfileScreen() }

        composable(
            route = "poll_details/{pollId}",
            arguments = listOf(navArgument("pollId") { type = NavType.IntType })
        ) {
            val pollId = it.arguments?.getInt("pollId") ?: 0
            val poll = generateDummyPolls().first { p -> p.id == pollId }
            PollDetailsScreen(poll, navController)
        }
    }
}


@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.AddPollV,
        BottomNavItem.MPollV,
        BottomNavItem.Profile
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                    }
                }
            )
        }
    }
}
