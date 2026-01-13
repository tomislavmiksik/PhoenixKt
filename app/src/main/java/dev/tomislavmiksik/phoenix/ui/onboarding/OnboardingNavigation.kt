package dev.tomislavmiksik.phoenix.ui.onboarding

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.serialization.Serializable

@Serializable
data object OnboardingGraph

@Serializable
data object OnboardingRoute

fun NavGraphBuilder.onboardingGraph(
    onOnboardingComplete: () -> Unit,
    onLaunchPermissionRequest: (Set<String>, (Set<String>) -> Unit) -> Unit,
) {
    navigation<OnboardingGraph>(
        startDestination = OnboardingRoute,
    ) {
        composable<OnboardingRoute> {
            val viewModel: OnboardingViewModel = hiltViewModel()

            OnboardingScreen(
                viewModel = viewModel,
                onNavigateToDashboard = onOnboardingComplete,
                onLaunchPermissionRequest = { permissions ->
                    onLaunchPermissionRequest(permissions) { granted ->
                        viewModel.trySendAction(OnboardingAction.PermissionsResult(granted))
                    }
                }
            )
        }
    }
}
