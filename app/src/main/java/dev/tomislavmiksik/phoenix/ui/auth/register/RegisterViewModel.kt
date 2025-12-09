package dev.tomislavmiksik.phoenix.ui.auth.register

import android.os.Parcelable
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.tomislavmiksik.phoenix.core.domain.repository.AuthRepository
import dev.tomislavmiksik.phoenix.ui.base.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    val authRepository: AuthRepository
) : BaseViewModel<RegisterState, RegisterEvent, RegisterAction>(
    initialState = RegisterState.emptyState
) {
    override fun handleAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.EmailChanged -> handleEmailChanged(action)
            is RegisterAction.UsernameChanged -> handleUsernameChanged(action)
            is RegisterAction.PasswordChanged -> handlePasswordChanged(action)
            is RegisterAction.ConfirmPasswordChanged -> handleConfirmPasswordChanged(action)
            is RegisterAction.FirstNameChanged -> handleFirstNameChanged(action)
            is RegisterAction.LastNameChanged -> handleLastNameChanged(action)
            is RegisterAction.RegisterButtonClick -> handleRegisterButtonClick()
            is RegisterAction.Internal.RegisterFailed -> handleRegisterFailed(action)
            is RegisterAction.Internal.RegisterSuccess -> handleRegisterSuccess()
        }
    }

    private fun handleEmailChanged(action: RegisterAction.EmailChanged) {
        mutableStateFlow.update { it.copy(email = action.email, errorMessage = null) }
    }

    private fun handleUsernameChanged(action: RegisterAction.UsernameChanged) {
        mutableStateFlow.update { it.copy(username = action.username, errorMessage = null) }
    }

    private fun handlePasswordChanged(action: RegisterAction.PasswordChanged) {
        mutableStateFlow.update { it.copy(password = action.password, errorMessage = null) }
    }

    private fun handleConfirmPasswordChanged(action: RegisterAction.ConfirmPasswordChanged) {
        mutableStateFlow.update { it.copy(confirmPassword = action.confirmPassword, errorMessage = null) }
    }

    private fun handleFirstNameChanged(action: RegisterAction.FirstNameChanged) {
        mutableStateFlow.update { it.copy(firstName = action.firstName, errorMessage = null) }
    }

    private fun handleLastNameChanged(action: RegisterAction.LastNameChanged) {
        mutableStateFlow.update { it.copy(lastName = action.lastName, errorMessage = null) }
    }

    private fun handleRegisterButtonClick() {
        // TODO: Implement registration logic
        mutableStateFlow.update { it.copy(isLoading = true, errorMessage = null) }
        // For now, just show an error
        mutableStateFlow.update {
            it.copy(
                isLoading = false,
                errorMessage = "Registration not yet implemented"
            )
        }
    }

    private fun handleRegisterFailed(action: RegisterAction.Internal.RegisterFailed) {
        mutableStateFlow.update {
            it.copy(
                isLoading = false,
                errorMessage = action.message
            )
        }
    }

    private fun handleRegisterSuccess() {
        mutableStateFlow.update { it.copy(isLoading = false) }
        sendEvent(RegisterEvent.NavigateToHome)
    }
}

@Parcelize
data class RegisterState(
    val email: String,
    val username: String,
    val password: String,
    val confirmPassword: String,
    val firstName: String,
    val lastName: String,
    val isLoading: Boolean,
    val errorMessage: String?
) : Parcelable {
    val isRegisterEnabled: Boolean
        get() = !isLoading &&
                email.isNotEmpty() &&
                username.isNotEmpty() &&
                password.isNotEmpty() &&
                confirmPassword.isNotEmpty() &&
                firstName.isNotEmpty() &&
                lastName.isNotEmpty()

    companion object {
        val emptyState = RegisterState(
            email = "",
            username = "",
            password = "",
            confirmPassword = "",
            firstName = "",
            lastName = "",
            isLoading = false,
            errorMessage = null
        )

        val debugState = RegisterState(
            email = "test11@user.com",
            username = "testuser",
            password = "password123",
            confirmPassword = "password123",
            firstName = "Test",
            lastName = "User",
            isLoading = false,
            errorMessage = null
        )
    }
}


sealed class RegisterEvent {
    data object NavigateToHome : RegisterEvent()
    data object NavigateToLogin : RegisterEvent()

    sealed class Internal : RegisterEvent() {
        data object RegisterFailed : Internal()
    }
}

sealed class RegisterAction {
    data class EmailChanged(val email: String) : RegisterAction()
    data class UsernameChanged(val username: String) : RegisterAction()
    data class PasswordChanged(val password: String) : RegisterAction()
    data class ConfirmPasswordChanged(val confirmPassword: String) : RegisterAction()
    data class FirstNameChanged(val firstName: String) : RegisterAction()
    data class LastNameChanged(val lastName: String) : RegisterAction()
    class RegisterButtonClick : RegisterAction()

    sealed class Internal : RegisterAction() {
        data class RegisterFailed(val message: String) : Internal()
        class RegisterSuccess : Internal()
    }
}