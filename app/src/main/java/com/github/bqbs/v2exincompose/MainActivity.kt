package com.github.bqbs.v2exincompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.github.bqbs.v2exincompose.MainDestinations.PROFILE
import com.github.bqbs.v2exincompose.ui.theme.V2exInComposeTheme

class MainActivity : ComponentActivity() {

    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        setContent {

            V2exInComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    NavGraph()
                }
            }
        }
    }
}

object MainDestinations {
    const val TOPICS = "/home/route_to_topics"
    const val PROFILE = "route_to_profile/{userid}"
    const val HOTS = "/home/route_to_hots"
}

sealed class Screen(val route: String, @StringRes val resourceId: Int) {
    object Topics : Screen(MainDestinations.TOPICS, R.string.topics)
    object Hots : Screen(MainDestinations.HOTS, R.string.hots)
    object Profile : Screen(MainDestinations.PROFILE, R.string.profile)
}

@ExperimentalFoundationApi
@Composable
fun NavGraph(
    startDestination: String = MainDestinations.TOPICS
) {
    val navController = rememberNavController()

    val actions = remember(navController) { MainActions(navController) }

    val items = listOf(
        Screen.Topics,
        Screen.Hots
    )
    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            if (currentDestination?.route?.startsWith("/home") == true) {
                BottomNavigation {
//                    val navBackStackEntry by navController.currentBackStackEntryAsState()
//                    val currentDestination = navBackStackEntry?.destination
                    items.forEach { screen ->
                        BottomNavigationItem(
                            icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                            label = { Text(stringResource(screen.resourceId)) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route },
                            onClick = { actions.navigate(navController, screen.route) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            Modifier.padding(innerPadding)
        ) {
            composable(MainDestinations.TOPICS) {
                TopicsPage(actions)
            }
            composable(MainDestinations.HOTS) {
                HotsPage(actions)
            }
            composable(
                MainDestinations.PROFILE,
                arguments = listOf(navArgument("userid") { type = NavType.LongType })
            ) {
                ProfilePage(actions, id = it.arguments?.getLong("userid"))
            }
        }
    }
}


class MainActions(navController: NavHostController) {

    val homePage: () -> Unit = {
        navigate(navController, MainDestinations.TOPICS)
    }

    val showProfile: (userid: Long?) -> Unit = {
        navigate(navController, "route_to_profile/$it")
    }

    val hotsPage: () -> Unit = {
        navigate(navController, MainDestinations.HOTS)
    }

    fun navigate(navController: NavHostController, route: String) {
        navController.navigate(route) {
//            popUpTo(navController.graph.findStartDestination().id) {
//                saveState = true
//            }
            launchSingleTop = true
            restoreState = true

            anim {
                enter = R.anim.in_from_right
                exit = R.anim.out_to_left
                popEnter = R.anim.in_from_right
                popExit = R.anim.out_to_left
            }
        }

    }

}