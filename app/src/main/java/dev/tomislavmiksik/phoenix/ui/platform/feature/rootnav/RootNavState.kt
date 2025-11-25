package dev.tomislavmiksik.phoenix.ui.platform.feature.rootnav

/**
 * Top-level navigation state that determines which flow the user should see.
 * This follows the state-based navigation pattern from Bitwarden.
 */
sealed class RootNavState {
    /**
     * User is not authenticated, show auth flow
     */
    data object Auth : RootNavState()

    /**
     * User is authenticated, show main app flow
     */
    data object Main : RootNavState()

    /**
     * App is loading initial state
     */
    data object Loading : RootNavState()
}
