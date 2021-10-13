package com.github.bqbs.v2exincompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.bqbs.v2exincompose.ui.theme.V2exInComposeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    const val TOPICS = "route_to_topics"
    const val PROFILE = "route_to_profile"
}

sealed class Screen(val route: String, @StringRes val resourceId: Int) {
    object Topics : Screen(MainDestinations.TOPICS, R.string.topics)
    object Profile : Screen(MainDestinations.PROFILE, R.string.profile)
}

@Composable
fun NavGraph(
    startDestination: String = MainDestinations.TOPICS
) {
    val navController = rememberNavController()

    val actions = remember(navController) { MainActions(navController) }

    val items = listOf(
        Screen.Topics,
        Screen.Profile
    )
    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                        label = { Text(stringResource(screen.resourceId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
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
            composable(MainDestinations.PROFILE) {
                ProfilePage(actions, userName = "fdppzrl", viewModel = viewModel())
            }
        }
    }
}


class MainActions(navController: NavHostController) {

    val homePage: () -> Unit = {
        navigate(navController, MainDestinations.TOPICS)
    }

    val showProfile: () -> Unit = {
        navigate(navController, MainDestinations.PROFILE)
    }

    private fun navigate(navController: NavHostController, route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true

            anim {
//                enter = R.anim.in_from_right
//                exit = R.anim.out_to_left
//                popEnter = R.anim.in_from_right
//                popExit = R.anim.out_to_left
            }
        }
    }

}