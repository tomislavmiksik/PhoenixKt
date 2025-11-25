package dev.tomislavmiksik.phoenix.ui.auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import dev.tomislavmiksik.phoenix.ui.login.LoginRoute
import dev.tomislavmiksik.phoenix.ui.login.loginDestination

/**
 * Auth navigation graph containing authentication-related screens.
 * This follows the Bitwarden pattern for nested navigation graphs.
 */
fun NavGraphBuilder.authGraph(
    onLoginSuccess: () -> Unit,
    navController: NavController,
) {
    navigation<AuthGraph>(
        startDestination = LoginRoute,
    ) {
        loginDestination(
            onNavigateToHome = onLoginSuccess,
        )

        // Add more auth screens here (e.g., Register, ForgotPassword)
        // registerDestination(...)
        // forgotPasswordDestination(...)
    }
}

/**
 * Root route for the auth navigation graph.
 */
const val AUTH_GRAPH_ROUTE = "auth"

/**
 * Sealed class representing the auth graph route.
 * Using a sealed class allows for type-safe navigation at the graph level.
 */
sealed class AuthGraph {
    companion object {
        const val route = AUTH_GRAPH_ROUTE
    }
}
