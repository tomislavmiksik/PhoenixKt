package dev.tomislavmiksik.phoenix.ui.platform.rootnav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.tomislavmiksik.phoenix.core.data.datastore.PhoenixPreferencesDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootNavViewModel @Inject constructor(
    private val preferencesDataSource: PhoenixPreferencesDataSource
) : ViewModel() {

    private val _rootNavState = MutableStateFlow<RootNavState>(RootNavState.Loading)
    val rootNavState: StateFlow<RootNavState> = _rootNavState.asStateFlow()

    init {
        observeOnboardingState()
    }

    private fun observeOnboardingState() {
        viewModelScope.launch {
            preferencesDataSource.onboardingCompletedFlow.collect { completed ->
                _rootNavState.value = if (completed) {
                    RootNavState.Main
                } else {
                    RootNavState.Onboarding
                }
            }
        }
    }

    fun onOnboardingComplete() {
        _rootNavState.value = RootNavState.Main
    }
}
