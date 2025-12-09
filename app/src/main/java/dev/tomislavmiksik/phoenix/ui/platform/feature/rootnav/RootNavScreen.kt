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
    // Navigate based on state changes (after initial load)
    LaunchedEffect(rootNavState) {
        // Skip navigation if we're still loading or if this is the initial composition
        // The startDestination will handle the initial state
        if (rootNavState == RootNavState.Loading) return@LaunchedEffect

        // Only navigate if the current destination doesn't match the desired state
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        val shouldNavigate = when (rootNavState) {
            RootNavState.Auth -> currentRoute != AuthGraph::class.qualifiedName
            RootNavState.Main -> currentRoute != MainGraph::class.qualifiedName
            RootNavState.Loading -> false
        }

        if (shouldNavigate) {
            when (rootNavState) {
                RootNavState.Auth -> {
                    navController.navigate(AuthGraph) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }

                RootNavState.Main -> {
                    navController.navigate(MainGraph) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }

                RootNavState.Loading -> {}
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
            // Set up navigation graph with dynamic start destination
            val startDestination = when (rootNavState) {
                RootNavState.Auth -> AuthGraph
                RootNavState.Main -> MainGraph
                RootNavState.Loading -> AuthGraph // Fallback (shouldn't reach here)
            }

            NavHost(
                navController = navController,
                startDestination = startDestination,
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
