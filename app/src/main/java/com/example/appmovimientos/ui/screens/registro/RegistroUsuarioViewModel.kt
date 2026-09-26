package com.example.appmovimientos.ui.screens.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovimientos.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistroUsuarioViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    var nombre: String = ""
        set(value) {
            field = value
            _uiState.update { it.copy(nombre = value) }
        }

    var correo: String = ""
        set(value) {
            field = value
            _uiState.update { it.copy(correo = value) }
        }

    var usuario: String = ""
        set(value) {
            field = value
            _uiState.update { it.copy(usuario = value) }
        }

    var contrasena: String = ""
        set(value) {
            field = value
            _uiState.update { it.copy(contrasena = value) }
        }

    data class RegistroUiState(
        val nombre: String = "",
        val correo: String = "",
        val usuario: String = "",
        val contrasena: String = "",
        val mostrarContrasena: Boolean = false,
        val errorMessage: String? = null,
        val isLoading: Boolean = false
    )

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    fun onNombreChange(newValue: String) {
        nombre = newValue
    }

    fun onCorreoChange(newValue: String) {
        correo = newValue
    }

    fun onUsuarioChange(newValue: String) {
        usuario = newValue
    }

    fun onContrasenaChange(newValue: String) {
        contrasena = newValue
    }

    fun toggleMostrarContrasena() {
        _uiState.update { it.copy(mostrarContrasena = !it.mostrarContrasena) }
    }

    fun registrarUsuario(onSuccess: () -> Unit) {
        val correoLimpio = correo.trim()
        when {
            nombre.isBlank() || correoLimpio.isBlank() || usuario.isBlank() || contrasena.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Todos los campos son obligatorios.") }
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(correoLimpio).matches() -> {
                _uiState.update { it.copy(errorMessage = "El correo no es válido.") }
            }
            contrasena.length < 6 -> {
                _uiState.update { it.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres.") }
            }
            else -> {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                viewModelScope.launch {
                    try {
                        authRepository.createUserWithEmailAndPassword(correoLimpio, contrasena)
                        val uid = authRepository.getCurrentUserUid()
                        if (uid != null) {
                            val userData = mapOf(
                                "nombre" to nombre,
                                "correo" to correoLimpio,
                                "usuario" to usuario
                            )
                            authRepository.saveUserData(uid, userData)
                            _uiState.update { it.copy(isLoading = false) }
                            onSuccess()
                        } else {
                            _uiState.update { it.copy(isLoading = false, errorMessage = "Error al obtener UID del usuario") }
                        }
                    } catch (e: Exception) {
                        _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                    }
                }
            }
        }
    }
}
