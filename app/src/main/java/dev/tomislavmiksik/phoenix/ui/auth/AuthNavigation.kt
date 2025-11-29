package dev.tomislavmiksik.phoenix.ui.auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import dev.tomislavmiksik.phoenix.ui.login.LoginRoute
import dev.tomislavmiksik.phoenix.ui.login.loginDestination
import kotlinx.serialization.Serializable


/**
 * Type-safe route for the auth navigation graph.
 * Using @Serializable data object for modern type-safe navigation.
 */
@Serializable
data object AuthGraph

/**
 * Auth navigation graph containing authentication-related screens.
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
