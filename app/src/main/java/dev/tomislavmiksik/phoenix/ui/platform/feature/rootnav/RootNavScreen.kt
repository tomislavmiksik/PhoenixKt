package dev.tomislavmiksik.phoenix.ui.platform.feature.rootnav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dev.tomislavmiksik.phoenix.ui.auth.AuthGraph
import dev.tomislavmiksik.phoenix.ui.auth.authGraph
import dev.tomislavmiksik.phoenix.ui.main.MainGraph
import dev.tomislavmiksik.phoenix.ui.main.mainGraph

/**
 * Root navigation screen that manages state-based navigation between
 * top-level flows (Auth and Main).
 *
 * This follows the Bitwarden pattern where navigation state is derived
 * from data layer state rather than manual navigation calls.
 */
@Composable
fun RootNavScreen(
    viewModel: RootNavViewModel = hiltViewModel(),
) {
    val rootNavState by viewModel.rootNavState.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    RootNavScreen(
        rootNavState = rootNavState,
        navController = navController,
        onLoginSuccess = viewModel::onLoginSuccess,
        onLogout = viewModel::onLogout,
    )
}

@Composable
private fun RootNavScreen(
    rootNavState: RootNavState,
    navController: NavHostController,
    onLoginSuccess: () -> Unit,
    onLogout: () -> Unit,
) {
    // Navigate based on state changes
    LaunchedEffect(rootNavState) {
        when (rootNavState) {
            RootNavState.Auth -> {
                navController.navigate(AuthGraph) {
                    // Clear back stack when navigating to auth
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
            RootNavState.Main -> {
                navController.navigate(MainGraph) {
                    // Clear back stack when navigating to main
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
            RootNavState.Loading -> {
                // Stay on loading, NavHost will show initial route
            }
        }
    }

    when (rootNavState) {
        RootNavState.Loading -> {
            // Show loading indicator while determining initial state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        else -> {
            // Set up navigation graph
            NavHost(
                navController = navController,
                startDestination = AuthGraph, // Default start, will be overridden by LaunchedEffect
            ) {
                authGraph(
                    onLoginSuccess = onLoginSuccess,
                    navController = navController,
                )

                mainGraph(
                    onLogout = onLogout,
                    navController = navController,
                )
            }
        }
    }
}
