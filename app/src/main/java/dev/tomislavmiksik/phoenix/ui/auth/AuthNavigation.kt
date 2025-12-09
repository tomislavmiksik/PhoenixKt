package dev.tomislavmiksik.phoenix.ui.auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import dev.tomislavmiksik.phoenix.ui.auth.login.LoginRoute
import dev.tomislavmiksik.phoenix.ui.auth.login.loginDestination
import dev.tomislavmiksik.phoenix.ui.auth.login.navigateToRegister
import dev.tomislavmiksik.phoenix.ui.auth.register.navigateToLogin
import dev.tomislavmiksik.phoenix.ui.auth.register.registerDestination
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
            onNavigateToRegister = {
                navController.navigateToRegister(
                    navOptions = androidx.navigation.navOptions {
                        launchSingleTop = true
                        restoreState = true
                    }
                )
            },
        )
        registerDestination(
            onNavigateToHome = onLoginSuccess,
            onNavigateToLogin = {
                navController.navigateToLogin(
                    navOptions = androidx.navigation.navOptions {
                        launchSingleTop = true
                    }
                )
            },
        )
    }
}
