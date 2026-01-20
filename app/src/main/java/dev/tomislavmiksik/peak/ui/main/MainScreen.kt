package dev.tomislavmiksik.peak.ui.main

import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.tomislavmiksik.peak.ui.activity.ActivityScreen
import dev.tomislavmiksik.peak.ui.home.HomeScreen
import dev.tomislavmiksik.peak.ui.progress.ProgressScreen
import dev.tomislavmiksik.peak.ui.main.components.BottomNavBar
import dev.tomislavmiksik.peak.ui.main.components.BottomNavDestination

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val selectedTab = when {
        currentDestination?.hasRoute<ActivityRoute>() == true -> BottomNavDestination.Activity
        currentDestination?.hasRoute<ProgressRoute>() == true -> BottomNavDestination.Progress
        else -> BottomNavDestination.Home
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavBar(
                currentDestination = selectedTab,
                onDestinationSelected = { destination ->
                    if (selectedTab != destination) {
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            enterTransition = { fadeIn(animationSpec = tween(150)) },
            exitTransition = { fadeOut(animationSpec = tween(150)) }
        ) {
            composable<HomeRoute> {
                HomeScreen()
            }
            composable<ActivityRoute> {
                ActivityScreen()
            }
            composable<ProgressRoute> {
                ProgressScreen()
            }
        }
    }
}
