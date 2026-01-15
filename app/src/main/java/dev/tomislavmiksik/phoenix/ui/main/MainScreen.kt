package dev.tomislavmiksik.phoenix.ui.main

import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.tomislavmiksik.phoenix.R
import dev.tomislavmiksik.phoenix.ui.dashboard.DashboardScreen
import dev.tomislavmiksik.phoenix.ui.main.components.BottomNavBar
import dev.tomislavmiksik.phoenix.ui.main.components.BottomNavDestination
import dev.tomislavmiksik.phoenix.ui.profile.ProfileScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val selectedTab = when {
        currentDestination?.hasRoute<ProfileRoute>() == true -> BottomNavDestination.Profile
        else -> BottomNavDestination.Dashboard
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            if (selectedTab == BottomNavDestination.Dashboard)
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary,
                    onClick = {}
                ) {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.common_add)
                    )
                }
        },
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
            startDestination = DashboardRoute,
            enterTransition = { fadeIn(animationSpec = tween(150)) },
            exitTransition = { fadeOut(animationSpec = tween(150)) }
        ) {
            composable<DashboardRoute> {
                DashboardScreen()
            }
            composable<ProfileRoute> {
                ProfileScreen()
            }
        }
    }
}
