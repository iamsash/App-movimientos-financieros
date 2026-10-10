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

    fun login(onNavigateMain: () -> Unit, onNavigateTerminos: () -> Unit) {
        val inputLimpio = correo.trim()
        if (inputLimpio.isBlank() || contrasena.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor ingresa correo o usuario y contraseña") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val emailToSign = if (inputLimpio.contains("@")) {
                    inputLimpio
                } else {
                    authRepository.getEmailByUsername(inputLimpio)
                        ?: throw IllegalArgumentException("El usuario ingresado no existe o no tiene correo asociado.")
                }
                authRepository.signInWithEmailAndPassword(emailToSign, contrasena)
                val uid = authRepository.getCurrentUserUid()
                if (uid != null) {
                    val hasAccepted = try {
                        authRepository.checkUserAcceptance(uid)
                    } catch (e: Exception) {
                        authRepository.signOut()
                        throw Exception("No se pudo verificar la aceptación de términos por error de conexión.")
                    }

                    _uiState.update { it.copy(isLoading = false) }
                    if (hasAccepted) {
                        onNavigateMain()
                    } else {
                        onNavigateTerminos()
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error al obtener usuario autenticado") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Error al iniciar sesión") }
            }
        }
    }
}
