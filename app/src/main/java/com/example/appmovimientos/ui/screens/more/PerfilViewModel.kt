package com.example.appmovimientos.ui.screens.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovimientos.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    data class PerfilUiState(
        val nombre: String = "",
        val correo: String = "",
        val usuario: String = "",
        val isEditing: Boolean = false,
        val isLoading: Boolean = true,
        val isSaving: Boolean = false,
        val errorMessage: String? = null,
        val successMessage: String? = null
    )

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        val uid = authRepository.getCurrentUserUid()
        if (uid != null) {
            viewModelScope.launch {
                try {
                    val profile = authRepository.getUserProfile(uid)
                    _uiState.update {
                        it.copy(
                            nombre = profile["nombre"] ?: "",
                            correo = profile["correo"] ?: "",
                            usuario = profile["usuario"] ?: "",
                            isLoading = false
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(errorMessage = e.message, isLoading = false) }
                }
            }
        } else {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Usuario no autenticado") }
        }
    }

    fun onNombreChange(newValue: String) {
        _uiState.update { it.copy(nombre = newValue) }
    }

    fun onUsuarioChange(newValue: String) {
        _uiState.update { it.copy(usuario = newValue) }
    }

    fun startEditing() {
        _uiState.update { it.copy(isEditing = true, errorMessage = null, successMessage = null) }
    }

    fun cancelEditing() {
        _uiState.update { it.copy(isEditing = false, errorMessage = null, successMessage = null) }
        loadProfile() // reset to original
    }

    fun saveProfile(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        if (currentState.nombre.isBlank() || currentState.usuario.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El nombre y el usuario son obligatorios.") }
            return
        }

        val uid = authRepository.getCurrentUserUid()
        if (uid == null) {
            _uiState.update { it.copy(errorMessage = "Usuario no autenticado.") }
            return
        }

        _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }
        viewModelScope.launch {
            try {
                authRepository.updateUserProfile(uid, currentState.nombre.trim(), currentState.usuario.trim())
                _uiState.update { it.copy(isSaving = false, isEditing = false, successMessage = "Perfil actualizado exitosamente") }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, errorMessage = e.message ?: "Error al actualizar perfil") }
            }
        }
    }
}
