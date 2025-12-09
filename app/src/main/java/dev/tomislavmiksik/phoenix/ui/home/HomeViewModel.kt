package dev.tomislavmiksik.phoenix.ui.home

import android.os.Parcelable
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.tomislavmiksik.phoenix.core.domain.repository.AuthRepository
import dev.tomislavmiksik.phoenix.ui.base.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val authRepository: AuthRepository
) : BaseViewModel<HomeState, HomeEvent, HomeAction>(
    initialState = HomeState.empty
) {
    override fun handleAction(action: HomeAction) {
        when (action) {
            is HomeAction.LogoutButtonClick -> handleLogoutButtonClick()
            is HomeAction.AboutButtonClick -> handleAboutButtonClick()
        }
    }

    private fun handleLogoutButtonClick() {
        mutableStateFlow.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = authRepository.logout()
            result.fold(
                onFailure = { exception ->
                    mutableStateFlow.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Logout failed: ${exception.message}"
                        )
                    }
                },
                onSuccess = {
                    mutableStateFlow.update { it.copy(isLoading = false) }
                    sendEvent(HomeEvent.NavigateToLogin)
                }
            )
        }
    }

    private fun handleAboutButtonClick() {
        sendEvent(HomeEvent.NavigateToAbout)
    }
}

@Parcelize
data class HomeState(
    val isLoading: Boolean,
    val errorMessage: String?
) : Parcelable {

    companion object {
        val empty = HomeState(
            isLoading = false,
            errorMessage = null
        )
    }
}

sealed class HomeEvent {
    data object NavigateToLogin : HomeEvent()
    data object NavigateToAbout : HomeEvent()

    sealed class Internal : HomeEvent() {
        data object LogoutFailed : Internal()
    }
}

sealed class HomeAction {
    data object LogoutButtonClick : HomeAction()
    data object AboutButtonClick : HomeAction()
}
