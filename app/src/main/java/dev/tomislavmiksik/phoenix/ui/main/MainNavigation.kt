package dev.tomislavmiksik.phoenix.ui.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import dev.tomislavmiksik.phoenix.ui.dashboard.DashboardScreen
import kotlinx.serialization.Serializable

@Serializable
data object MainGraph

@Serializable
data object DashboardRoute

fun NavGraphBuilder.mainGraph(
    navController: NavController,
) {
    navigation<MainGraph>(
        startDestination = DashboardRoute,
    ) {
        composable<DashboardRoute> {
            DashboardScreen()
        }
    }
}
