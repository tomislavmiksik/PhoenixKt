package dev.tomislavmiksik.phoenix.ui.login

import android.os.Parcelable
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.tomislavmiksik.phoenix.ui.base.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    // TODO: Inject AuthRepository when available
) : BaseViewModel<LoginState, LoginEvent, LoginAction>(
    initialState = LoginState(
        email = "",
        password = "",
        isLoading = false,
        errorMessage = null,
    )
) {
    override fun handleAction(action: LoginAction) {
        when (action) {
            is LoginAction.EmailChanged -> handleEmailChanged(action)
            is LoginAction.PasswordChanged -> handlePasswordChanged(action)
            is LoginAction.LoginButtonClick -> handleLoginButtonClick()
            is LoginAction.Internal.LoginSuccess -> handleLoginSuccess()
            is LoginAction.Internal.LoginError -> handleLoginError(action)
        }
    }

    private fun handleEmailChanged(action: LoginAction.EmailChanged) {
        mutableStateFlow.update {
            it.copy(email = action.email, errorMessage = null)
        }
    }

    private fun handlePasswordChanged(action: LoginAction.PasswordChanged) {
        mutableStateFlow.update {
            it.copy(password = action.password, errorMessage = null)
        }
    }

    private fun handleLoginButtonClick() {
        // Update state to loading
        mutableStateFlow.update { it.copy(isLoading = true, errorMessage = null) }

        // Simulate login API call
        viewModelScope.launch {
            delay(1500) // Simulate network delay

            // TODO: Replace with actual authentication logic
            if (state.email.isNotEmpty() && state.password.isNotEmpty()) {
                sendAction(LoginAction.Internal.LoginSuccess)
            } else {
                sendAction(LoginAction.Internal.LoginError("Please enter email and password"))
            }
        }
    }

    private fun handleLoginSuccess() {
        mutableStateFlow.update { it.copy(isLoading = false) }
        sendEvent(LoginEvent.NavigateToHome)
    }

    private fun handleLoginError(action: LoginAction.Internal.LoginError) {
        mutableStateFlow.update {
            it.copy(isLoading = false, errorMessage = action.message)
        }
    }
}

@Parcelize
data class LoginState(
    val email: String,
    val password: String,
    val isLoading: Boolean,
    val errorMessage: String?,
) : Parcelable {
    val isLoginEnabled: Boolean
        get() = !isLoading && email.isNotEmpty() && password.isNotEmpty()
}

sealed class LoginEvent {
    data object NavigateToHome : LoginEvent()
    data object  NavigateToRegister: LoginEvent()
}

sealed class LoginAction {
    data class EmailChanged(val email: String) : LoginAction()
    data class PasswordChanged(val password: String) : LoginAction()
    data object LoginButtonClick : LoginAction()

    sealed class Internal : LoginAction() {
        data object LoginSuccess : Internal()
        data class LoginError(val message: String) : Internal()
    }
}
