package dev.tomislavmiksik.phoenix.ui.platform.feature.rootnav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.tomislavmiksik.phoenix.core.domain.repository.AuthRepository
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
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _rootNavState = MutableStateFlow<RootNavState>(RootNavState.Loading)
    val rootNavState: StateFlow<RootNavState> = _rootNavState.asStateFlow()

    init {
        observeUserState()
    }

    private fun observeUserState() {
        viewModelScope.launch {
            authRepository.getAuthFlowState().collect { userState ->
                _rootNavState.value = when {
                    userState != null -> RootNavState.Main
                    else -> RootNavState.Auth
                }
            }
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
