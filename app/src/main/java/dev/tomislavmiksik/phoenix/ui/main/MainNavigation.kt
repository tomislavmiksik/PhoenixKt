package dev.tomislavmiksik.phoenix.ui.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import dev.tomislavmiksik.phoenix.ui.home.HomeRoute
import dev.tomislavmiksik.phoenix.ui.home.homeDestination

/**
 * Main navigation graph containing authenticated user screens.
 * This follows the Bitwarden pattern for nested navigation graphs.
 */
fun NavGraphBuilder.mainGraph(
    onLogout: () -> Unit,
    navController: NavController,
) {
    navigation<MainGraph>(
        startDestination = HomeRoute,
    ) {
        homeDestination(
            onNavigateToLogin = onLogout,
        )

        // Add more main screens here (e.g., WorkoutList, Profile, Settings)
        // workoutListDestination(...)
        // profileDestination(...)
        // settingsDestination(...)
    }
}

/**
 * Root route for the main navigation graph.
 */
const val MAIN_GRAPH_ROUTE = "main"

/**
 * Sealed class representing the main graph route.
 * Using a sealed class allows for type-safe navigation at the graph level.
 */
sealed class MainGraph {
    companion object {
        const val route = MAIN_GRAPH_ROUTE
    }
}
