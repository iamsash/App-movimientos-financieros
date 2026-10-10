package com.example.appmovimientos.ui.screens.terminos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovimientos.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LegacyAceptacionViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    data class LegacyUiState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow(LegacyUiState())
    val uiState: StateFlow<LegacyUiState> = _uiState.asStateFlow()

    fun aceptarTerminos(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val uid = authRepository.getCurrentUserUid()
        if (uid == null) {
            onError("Usuario no autenticado")
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                authRepository.updateTerminosAcceptance(uid)
                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                onError(e.message ?: "Error al guardar aceptación")
            }
        }
    }
}
