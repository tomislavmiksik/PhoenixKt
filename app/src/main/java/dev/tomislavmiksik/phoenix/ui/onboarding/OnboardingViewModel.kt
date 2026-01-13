package dev.tomislavmiksik.phoenix.ui.onboarding

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.tomislavmiksik.phoenix.core.data.datastore.PhoenixPreferencesDataSource
import dev.tomislavmiksik.phoenix.core.domain.repository.HealthConnectRepository
import dev.tomislavmiksik.phoenix.ui.base.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val healthConnectRepository: HealthConnectRepository,
    private val preferencesDataSource: PhoenixPreferencesDataSource,
) : BaseViewModel<OnboardingState, OnboardingEvent, OnboardingAction>(
    initialState = OnboardingState()
) {

    init {
        checkHealthConnectAvailability()
    }

    override fun handleAction(action: OnboardingAction) {
        when (action) {
            is OnboardingAction.RequestPermissions -> handleRequestPermissions()
            is OnboardingAction.PermissionsResult -> handlePermissionsResult(action.granted)
            is OnboardingAction.Internal.HealthConnectAvailable -> handleHealthConnectAvailable(
                action.available
            )
        }
    }

    private fun checkHealthConnectAvailability() {
        viewModelScope.launch {
            val isAvailable = healthConnectRepository.isAvailable()
            sendAction(OnboardingAction.Internal.HealthConnectAvailable(isAvailable))
        }
    }

    private fun checkHealthConnectPermissionGranted() {
        viewModelScope.launch {
            healthConnectRepository.hasAllPermissions()
        }
    }

    private fun handleHealthConnectAvailable(available: Boolean) {
        mutableStateFlow.update {
            it.copy(
                isLoading = false,
                isHealthConnectAvailable = available
            )
        }
    }

    private fun handleRequestPermissions() {
        sendEvent(OnboardingEvent.LaunchPermissionRequest(healthConnectRepository.getRequiredPermissions()))
    }

    private fun handlePermissionsResult(granted: Set<String>) {
        viewModelScope.launch {
            val hasAllPermissions = healthConnectRepository.hasAllPermissions()
            if (hasAllPermissions || granted.isNotEmpty()) {
                preferencesDataSource.setOnboardingCompleted(true)
                sendEvent(OnboardingEvent.NavigateToDashboard)
            } else {
                mutableStateFlow.update {
                    it.copy(errorMessage = "Permissions required to continue")
                }
            }
        }
    }
}

data class OnboardingState(
    val isLoading: Boolean = true,
    val isHealthConnectAvailable: Boolean = false,
    val isPermissionGranted: Boolean = false,
    val errorMessage: String? = null
)

sealed class OnboardingEvent {
    data class LaunchPermissionRequest(val permissions: Set<String>) : OnboardingEvent()
    data object NavigateToDashboard : OnboardingEvent()
}

sealed class OnboardingAction {
    data object RequestPermissions : OnboardingAction()
    data class PermissionsResult(val granted: Set<String>) : OnboardingAction()

    sealed class Internal : OnboardingAction() {
        data class HealthConnectAvailable(val available: Boolean) : Internal()
    }
}
