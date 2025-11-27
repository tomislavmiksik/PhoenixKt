package dev.tomislavmiksik.phoenix.ui.home

import android.os.Parcelable
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.tomislavmiksik.phoenix.ui.base.BaseViewModel
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    // TODO: Inject repositories when available
) : BaseViewModel<HomeState, HomeEvent, HomeAction>(
    initialState = HomeState()
) {
    override fun handleAction(action: HomeAction) {
        when (action) {
            is HomeAction.LogoutButtonClick -> handleLogoutButtonClick()
            is HomeAction.AboutButtonClick -> handleAboutButtonClick()
        }
    }

    private fun handleLogoutButtonClick() {
        // TODO: Call AuthRepository.logout()
        sendEvent(HomeEvent.NavigateToLogin)
    }

    private fun handleAboutButtonClick() {
        sendEvent(HomeEvent.NavigateToAbout)
    }
}

@Parcelize
data class HomeState(
    val placeholder: String = ""
) : Parcelable

sealed class HomeEvent {
    data object NavigateToLogin : HomeEvent()
    data object NavigateToAbout : HomeEvent()
}

sealed class HomeAction {
    data object LogoutButtonClick : HomeAction()
    data object AboutButtonClick : HomeAction()
}
