package dev.tomislavmiksik.phoenix.ui.auth.login

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import dev.tomislavmiksik.phoenix.ui.auth.register.RegisterRoute
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
    onNavigateToRegister: () -> Unit,
) {
    composable<LoginRoute> {
        LoginScreen(
            onNavigateToHome = onNavigateToHome,
            onNavigateToRegister = onNavigateToRegister,
        )
    }
}

fun NavController.navigateToRegister(
    navOptions: NavOptions? = null
) {
    this.navigate(route = RegisterRoute, navOptions = navOptions)
}