package dev.tomislavmiksik.phoenix.ui.auth.login

import android.os.Parcelable
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.tomislavmiksik.phoenix.core.domain.repository.AuthRepository
import dev.tomislavmiksik.phoenix.core.util.NetworkErrorHandler
import dev.tomislavmiksik.phoenix.ui.base.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val authRepository: AuthRepository,
) : BaseViewModel<LoginState, LoginEvent, LoginAction>(
    initialState = LoginState.debug
) {
    override fun handleAction(action: LoginAction) {
        when (action) {
            is LoginAction.UsernameChanged -> handleEmailChanged(action)
            is LoginAction.PasswordChanged -> handlePasswordChanged(action)
            is LoginAction.LoginButtonClick -> handleLoginButtonClick()
            is LoginAction.Internal.LoginSuccess -> handleLoginSuccess()
            is LoginAction.Internal.LoginError -> handleLoginError(action)
            is LoginAction.RegisterButtonClick -> handleRegisterButtonClick()
        }
    }

    private fun handleEmailChanged(action: LoginAction.UsernameChanged) {
        mutableStateFlow.update {
            it.copy(username = action.email, errorMessage = null)
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
            if (state.username.isNotEmpty() && state.password.isNotEmpty()) {
                val response = runCatching {
                    authRepository.login(
                        email = state.username.trim(),
                        password = state.password
                    )
                }

                response.fold(
                    onSuccess = { token ->
                        sendAction(LoginAction.Internal.LoginSuccess)
                    },
                    onFailure = { exception ->
                        val errorMessage = NetworkErrorHandler.getErrorMessage(exception)
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

    private fun handleRegisterButtonClick() {
        sendEvent(LoginEvent.NavigateToRegister)
    }
}

@Parcelize
data class LoginState(
    val username: String,
    val password: String,
    val isLoading: Boolean,
    val errorMessage: String?,
) : Parcelable {
    val isLoginEnabled: Boolean
        get() = !isLoading && username.isNotEmpty() && password.isNotEmpty()

    companion object HelperStates {
        val empty: LoginState = LoginState(
            username = "",
            password = "",
            isLoading = false,
            errorMessage = null,
        )

        val debug: LoginState = LoginState(
            username = "user1234",
            password = "user1234",
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
    data class UsernameChanged(val email: String) : LoginAction()
    data class PasswordChanged(val password: String) : LoginAction()
    data object LoginButtonClick : LoginAction()
    data object RegisterButtonClick : LoginAction()


    sealed class Internal : LoginAction() {
        data object LoginSuccess : Internal()
        data class LoginError(val message: String) : Internal()
    }
}
