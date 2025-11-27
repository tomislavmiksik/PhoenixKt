package dev.tomislavmiksik.phoenix.ui.login

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

/**
 * Type-safe route for Login screen using Kotlin Serialization.
 * This follows the Bitwarden pattern for type-safe navigation.
 */
@Serializable
data object LoginRoute

/**
 * Extension function to add the Login destination to the navigation graph.
 * This follows the Bitwarden pattern of creating type-safe NavGraphBuilder extensions.
 */
fun NavGraphBuilder.loginDestination(
    onNavigateToHome: () -> Unit,
) {
    composable<LoginRoute> {
        LoginScreen(
            onNavigateToHome = onNavigateToHome,
        )
    }
}

/**
 * Extension function to navigate to the Login screen.
 * This follows the Bitwarden pattern of creating type-safe NavController extensions.
 */
fun NavController.navigateToLogin(
    navOptions: NavOptions? = null,
) {
    this.navigate(route = LoginRoute, navOptions = navOptions)
}
