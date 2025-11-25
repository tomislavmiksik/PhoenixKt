package dev.tomislavmiksik.phoenix.ui.platform.feature.rootnav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel that manages the root navigation state.
 * Determines which top-level flow (Auth or Main) should be displayed.
 *
 * This follows the state-based navigation pattern from Bitwarden where
 * the navigation state is derived from data layer state (e.g., user authentication).
 */
@HiltViewModel
class RootNavViewModel @Inject constructor(
    // TODO: Inject AuthRepository when available
    // private val authRepository: AuthRepository
) : ViewModel() {

    private val _rootNavState = MutableStateFlow<RootNavState>(RootNavState.Loading)
    val rootNavState: StateFlow<RootNavState> = _rootNavState.asStateFlow()

    init {
        observeUserState()
    }

    private fun observeUserState() {
        viewModelScope.launch {
            // TODO: Observe user authentication state from AuthRepository
            // For now, default to Auth flow
            // authRepository.userStateFlow.collect { userState ->
            //     _rootNavState.value = when {
            //         userState.isLoggedIn -> RootNavState.Main
            //         else -> RootNavState.Auth
            //     }
            // }

            // Temporary: Default to Auth flow
            _rootNavState.value = RootNavState.Auth
        }
    }

    /**
     * Called when user logs out
     */
    fun onLogout() {
        _rootNavState.value = RootNavState.Auth
    }

    /**
     * Called when user logs in successfully
     */
    fun onLoginSuccess() {
        _rootNavState.value = RootNavState.Main
    }
}
