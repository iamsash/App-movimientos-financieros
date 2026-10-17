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
import java.util.Calendar

class ReportsViewModel(
    private val movementRepository: MovementRepository = MovementRepository()
) : ViewModel() {

    data class ReportsUiState(
        val movimientos: List<Expense> = emptyList(),
        val categorias: List<Category> = emptyList(),
        val cuentas: List<Account> = emptyList(),
        val selectedPeriod: String = "Todo",
        val selectedTab: String = "Mes",
        val sortOrder: String = "Mayor a menor",
        val selectedYear: String = Calendar.getInstance().get(Calendar.YEAR).toString(),
        val sortOrderTotal: String = "Mayor a menor",
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
                    _uiState.value.copy(
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

    fun setPeriod(period: String) {
        _uiState.update { it.copy(selectedPeriod = period) }
    }

    fun setTab(tab: String) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun setSortOrder(order: String) {
        _uiState.update { it.copy(sortOrder = order) }
    }

    fun setSelectedYear(year: String) {
        _uiState.update { it.copy(selectedYear = year) }
    }

    fun setSortOrderTotal(order: String) {
        _uiState.update { it.copy(sortOrderTotal = order) }
    }

    fun getFilteredMovimientos(): List<Expense> {
        val movimientos = _uiState.value.movimientos
        val periodo = _uiState.value.selectedPeriod
        val calendar = Calendar.getInstance()
        return when (periodo) {
            "Mes Actual" -> {
                val mesActual = calendar.get(Calendar.MONTH) + 1
                val anioActual = calendar.get(Calendar.YEAR)
                movimientos.filter { mov ->
                    try {
                        val partes = mov.date.split("/")
                        partes.size == 3 && partes[1].toInt() == mesActual && partes[2].toInt() == anioActual
                    } catch (e: Exception) { false }
                }
            }
            "Últimos 3 Meses" -> movimientos.take(100)
            "Último Año" -> {
                val anioActual = calendar.get(Calendar.YEAR)
                movimientos.filter { mov ->
                    try {
                        val partes = mov.date.split("/")
                        partes.size == 3 && partes[2].toInt() == anioActual
                    } catch (e: Exception) { false }
                }
            }
            else -> movimientos
        }
    }
}
