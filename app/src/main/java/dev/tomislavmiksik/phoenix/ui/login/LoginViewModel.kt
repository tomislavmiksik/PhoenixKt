package dev.tomislavmiksik.phoenix.ui.login

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
class LoginViewModel @Inject constructor(
    val authRepository: AuthRepository,
) : BaseViewModel<LoginState, LoginEvent, LoginAction>(
    initialState = LoginState.debug()
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
        mutableStateFlow.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            if (state.email.isNotEmpty() && state.password.isNotEmpty()) {
                authRepository.login(
                    email = state.email.trim(),
                    password = state.password
                ).fold(
                    onSuccess = { token ->
                        sendAction(LoginAction.Internal.LoginSuccess)
                    },
                    onFailure = { exception ->
                        val errorMessage = exception.message ?: "Unknown error occurred"
                        sendAction(LoginAction.Internal.LoginError(errorMessage))
                    }
                )

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

    companion object HelperStates {
        fun empty(): LoginState = LoginState(
            email = "",
            password = "",
            isLoading = false,
            errorMessage = null,
        )

        fun debug(): LoginState = LoginState(
            email = "test@user.com",
            password = "user123",
            isLoading = false,
            errorMessage = null,
        )
    }
}

sealed class LoginEvent {
    data object NavigateToHome : LoginEvent()
    data object NavigateToRegister : LoginEvent()
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
