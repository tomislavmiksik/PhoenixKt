package dev.tomislavmiksik.phoenix.ui.auth.register

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import dev.tomislavmiksik.phoenix.ui.auth.login.LoginRoute
import kotlinx.serialization.Serializable


@Serializable
data object RegisterRoute


/**
 * Extension function to add the Register destination to the navigation graph.
 * This follows the Bitwarden pattern of creating type-safe NavGraphBuilder extensions.
 */
fun NavGraphBuilder.registerDestination(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    composable<RegisterRoute> {
        RegisterScreen(
            onNavigateToHome = onNavigateToHome,
            onNavigateToLogin = onNavigateToLogin,
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