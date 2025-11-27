package dev.tomislavmiksik.phoenix.ui.about

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

/**
 * Type-safe route for About screen using Kotlin Serialization.
 */
@Serializable
data object AboutRoute

/**
 * Extension function to add the About destination to the navigation graph.
 */
fun NavGraphBuilder.aboutDestination(
    onNavigateBack: () -> Unit,
) {
    composable<AboutRoute> {
        AboutScreen(
            onNavigateBack = onNavigateBack,
        )
    }
}

/**
 * Extension function to navigate to the About screen.
 */
fun NavController.navigateToAbout(
    navOptions: NavOptions? = null,
) {
    this.navigate(route = AboutRoute, navOptions = navOptions)
}
