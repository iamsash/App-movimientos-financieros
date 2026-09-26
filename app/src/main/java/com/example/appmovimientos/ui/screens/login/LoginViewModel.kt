package com.example.appmovimientos.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovimientos.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    var correo: String = ""
        set(value) {
            field = value
            _uiState.update { it.copy(correo = value) }
        }

    var contrasena: String = ""
        set(value) {
            field = value
            _uiState.update { it.copy(contrasena = value) }
        }

    data class LoginUiState(
        val correo: String = "",
        val contrasena: String = "",
        val mostrarContrasena: Boolean = false,
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onCorreoChange(newValue: String) {
        correo = newValue
    }

    fun onContrasenaChange(newValue: String) {
        contrasena = newValue
    }

    fun toggleMostrarContrasena() {
        _uiState.update { it.copy(mostrarContrasena = !it.mostrarContrasena) }
    }

    fun login(onSuccess: () -> Unit) {
        val correoLimpio = correo.trim()
        if (correoLimpio.isBlank() || contrasena.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor ingresa correo y contraseña") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                authRepository.signInWithEmailAndPassword(correoLimpio, contrasena)
                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
