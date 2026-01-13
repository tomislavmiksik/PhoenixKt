package dev.tomislavmiksik.phoenix.ui.platform.rootnav

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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dev.tomislavmiksik.phoenix.ui.main.MainGraph
import dev.tomislavmiksik.phoenix.ui.main.mainGraph
import dev.tomislavmiksik.phoenix.ui.onboarding.OnboardingGraph
import dev.tomislavmiksik.phoenix.ui.onboarding.onboardingGraph

@Composable
fun RootNavScreen(
    onLaunchPermissionRequest: (Set<String>, (Set<String>) -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RootNavViewModel = hiltViewModel(),
) {
    val rootNavState by viewModel.rootNavState.collectAsStateWithLifecycle()

    when (rootNavState) {
        RootNavState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        RootNavState.Onboarding -> {
            RootNavHost(
                startDestination = OnboardingGraph,
                onOnboardingComplete = { viewModel.onOnboardingComplete() },
                onLaunchPermissionRequest = onLaunchPermissionRequest,
                modifier = modifier,
            )
        }

        RootNavState.Main -> {
            RootNavHost(
                startDestination = MainGraph,
                onOnboardingComplete = { },
                onLaunchPermissionRequest = onLaunchPermissionRequest,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun RootNavHost(
    startDestination: Any,
    onOnboardingComplete: () -> Unit,
    onLaunchPermissionRequest: (Set<String>, (Set<String>) -> Unit) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        onboardingGraph(
            onOnboardingComplete = onOnboardingComplete,
            onLaunchPermissionRequest = onLaunchPermissionRequest,
        )

        mainGraph(
            navController = navController,
        )
    }
}
