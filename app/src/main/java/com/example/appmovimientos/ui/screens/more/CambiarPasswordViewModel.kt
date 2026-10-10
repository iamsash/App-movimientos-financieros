package com.example.appmovimientos.ui.screens.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovimientos.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CambiarPasswordViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    data class CambiarPasswordUiState(
        val currentPass: String = "",
        val newPass: String = "",
        val confirmPass: String = "",
        val showCurrent: Boolean = false,
        val showNew: Boolean = false,
        val showConfirm: Boolean = false,
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val successMessage: String? = null
    )

    private val _uiState = MutableStateFlow(CambiarPasswordUiState())
    val uiState: StateFlow<CambiarPasswordUiState> = _uiState.asStateFlow()

    fun onCurrentPassChange(value: String) {
        _uiState.update { it.copy(currentPass = value, errorMessage = null, successMessage = null) }
    }

    fun onNewPassChange(value: String) {
        _uiState.update { it.copy(newPass = value, errorMessage = null, successMessage = null) }
    }

    fun onConfirmPassChange(value: String) {
        _uiState.update { it.copy(confirmPass = value, errorMessage = null, successMessage = null) }
    }

    fun toggleShowCurrent() {
        _uiState.update { it.copy(showCurrent = !it.showCurrent) }
    }

    fun toggleShowNew() {
        _uiState.update { it.copy(showNew = !it.showNew) }
    }

    fun toggleShowConfirm() {
        _uiState.update { it.copy(showConfirm = !it.showConfirm) }
    }

    fun changePassword(onSuccess: () -> Unit) {
        val state = _uiState.value
        when {
            state.currentPass.isBlank() || state.newPass.isBlank() || state.confirmPass.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Todos los campos son obligatorios.") }
            }
            state.newPass.length < 6 -> {
                _uiState.update { it.copy(errorMessage = "La nueva contraseña debe tener al menos 6 caracteres.") }
            }
            state.newPass != state.confirmPass -> {
                _uiState.update { it.copy(errorMessage = "Las nuevas contraseñas no coinciden.") }
            }
            state.currentPass == state.newPass -> {
                _uiState.update { it.copy(errorMessage = "La nueva contraseña no puede ser igual a la actual.") }
            }
            else -> {
                _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
                viewModelScope.launch {
                    try {
                        authRepository.changePassword(state.currentPass, state.newPass)
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                currentPass = "",
                                newPass = "",
                                confirmPass = "",
                                successMessage = "Contraseña cambiada correctamente."
                            )
                        }
                        onSuccess()
                    } catch (e: Exception) {
                        val friendlyMessage = when {
                            e.message?.contains("password", true) == true || e.message?.contains("credential", true) == true ->
                                "La contraseña actual es incorrecta."
                            e.message?.contains("network", true) == true ->
                                "Error de conexión. Verifica tu red e inténtalo de nuevo."
                            else -> "No se pudo cambiar la contraseña. Inténtalo de nuevo."
                        }
                        _uiState.update { it.copy(isLoading = false, errorMessage = friendlyMessage) }
                    }
                }
            }
        }
    }
}
