package dev.tomislavmiksik.phoenix.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.tomislavmiksik.phoenix.ui.platform.base.util.EventsEffect

/**
 * Home screen - main dashboard after login.
 */
@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            is HomeEvent.NavigateToLogin -> onNavigateToLogin()
        }
    }

    HomeScreenContent(
        state = state,
        onLogoutClick = { viewModel.trySendAction(HomeAction.LogoutButtonClick) },
    )
}

@Composable
private fun HomeScreenContent(
    state: HomeState,
    onLogoutClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to Phoenix!",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your fitness journey starts here",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(48.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Getting Started",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Track your workouts, monitor your progress, and achieve your fitness goals.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Logout")
        }
    }
}
