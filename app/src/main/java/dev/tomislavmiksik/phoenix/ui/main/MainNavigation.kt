package dev.tomislavmiksik.phoenix.ui.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import dev.tomislavmiksik.phoenix.ui.about.aboutDestination
import dev.tomislavmiksik.phoenix.ui.home.HomeRoute
import dev.tomislavmiksik.phoenix.ui.home.homeDestination
import kotlinx.serialization.Serializable

/**
 * Type-safe route for the main navigation graph.
 * Using @Serializable data object for modern type-safe navigation.
 */
@Serializable
data object MainGraph


/**
 * Main navigation graph containing authenticated user screens.
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
            onNavigateToAbout = {
                navController.navigate(dev.tomislavmiksik.phoenix.ui.about.AboutRoute)
            },
        )

        aboutDestination(
            onNavigateBack = {
                navController.navigateUp()
            },
        )

        // Add more main screens here (e.g., WorkoutList, Profile, Settings)
        // workoutListDestination(...)
        // profileDestination(...)
        // settingsDestination(...)
    }
}
