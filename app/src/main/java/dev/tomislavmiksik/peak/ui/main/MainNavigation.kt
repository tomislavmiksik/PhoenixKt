package dev.tomislavmiksik.peak.ui.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.serialization.Serializable

@Serializable
data object MainGraph

@Serializable
data object MainRoute

@Serializable
data object HomeRoute

@Serializable
data object ActivityRoute

@Serializable
data object LogbookRoute

fun NavGraphBuilder.mainGraph() {
    navigation<MainGraph>(
        startDestination = MainRoute,
    ) {
        composable<MainRoute> {
            MainScreen()
        }
    }
}
