package dev.tomislavmiksik.phoenix.ui.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

/**
 * Type-safe route for Home screen using Kotlin Serialization.
 */
@Serializable
object HomeRoute

/**
 * Extension function to add the Home destination to the navigation graph.
 */
fun NavGraphBuilder.homeDestination(
    onNavigateToLogin: () -> Unit,
) {
    composable<HomeRoute> {
        HomeScreen(
            onNavigateToLogin = onNavigateToLogin,
        )
    }
}

/**
 * Extension function to navigate to the Home screen.
 */
fun NavController.navigateToHome(
    navOptions: NavOptions? = null,
) {
    this.navigate(route = HomeRoute, navOptions = navOptions)
}
