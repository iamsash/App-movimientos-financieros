package com.example.appmovimientos.ui.screens.reportes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovimientos.data.model.Account
import com.example.appmovimientos.data.model.Category
import com.example.appmovimientos.data.model.Expense
import com.example.appmovimientos.data.repository.MovementRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReportsViewModel(
    private val movementRepository: MovementRepository = MovementRepository()
) : ViewModel() {

    data class ReportsUiState(
        val movimientos: List<Expense> = emptyList(),
        val categorias: List<Category> = emptyList(),
        val cuentas: List<Account> = emptyList(),
        val isLoading: Boolean = true,
        val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                combine(
                    movementRepository.getMovimientosFlow(),
                    movementRepository.getCategoriasFlow(),
                    movementRepository.getCuentasFlow()
                ) { movimientos, categorias, cuentas ->
                    ReportsUiState(
                        movimientos = movimientos,
                        categorias = categorias,
                        cuentas = cuentas,
                        isLoading = false
                    )
                }.collect { state ->
                    _uiState.value = state
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message, isLoading = false) }
            }
        }
    }
}
