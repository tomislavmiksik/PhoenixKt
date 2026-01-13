package dev.tomislavmiksik.phoenix.ui.platform.rootnav

sealed class RootNavState {
    data object Loading : RootNavState()
    data object Onboarding : RootNavState()
    data object Main : RootNavState()
}
