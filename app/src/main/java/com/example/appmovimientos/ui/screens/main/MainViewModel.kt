package com.example.appmovimientos.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovimientos.data.model.Account
import com.example.appmovimientos.data.model.Category
import com.example.appmovimientos.data.model.Expense
import com.example.appmovimientos.data.repository.AccountRepository
import com.example.appmovimientos.data.repository.AuthRepository
import com.example.appmovimientos.data.repository.CategoryRepository
import com.example.appmovimientos.data.repository.MovementRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(
    private val accountRepository: AccountRepository = AccountRepository(),
    private val categoryRepository: CategoryRepository = CategoryRepository(),
    private val movementRepository: MovementRepository = MovementRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    data class MainUiState(
        val cuentas: List<Account> = emptyList(),
        val categorias: Map<String, Category> = emptyMap(),
        val movimientos: List<Expense> = emptyList(),
        val balanceTotal: Double = 0.0,
        val gastoTotal: Double = 0.0,
        val ingresoTotal: Double = 0.0,
        val gastosMensuales: Map<String, Double> = emptyMap(),
        val categoriasMasGastadas: List<Pair<String, Double>> = emptyList(),
        val periodoSeleccionado: String = "Mes Actual",
        val isLoading: Boolean = true,
        val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                combine(
                    accountRepository.getAccountsFlow(),
                    categoryRepository.getCategoriesFlow(),
                    movementRepository.getMovimientosFlow()
                ) { cuentas, categoriasList, movimientosList ->
                    val balanceTotal = cuentas.sumOf { it.balance }

                    var gasto = 0.0
                    var ingreso = 0.0
                    val gastosPorMes = mutableMapOf<String, Double>()
                    val gastosPorCategoria = mutableMapOf<String, Double>()

                    for (mov in movimientosList) {
                        val monto = mov.amount.toDoubleOrNull() ?: 0.0
                        if (mov.type == "Gasto") {
                            gasto += monto
                            gastosPorCategoria[mov.categoryId] = (gastosPorCategoria[mov.categoryId] ?: 0.0) + monto
                        } else {
                            ingreso += monto
                        }

                        try {
                            val partes = mov.date.split("/")
                            if (partes.size == 3 && mov.type == "Gasto") {
                                val mesAnio = "${partes[1]}/${partes[2]}"
                                gastosPorMes[mesAnio] = (gastosPorMes[mesAnio] ?: 0.0) + monto
                            }
                        } catch (e: Exception) {}
                    }

                    val categoriasMap = categoriasList.associateBy { it.id }
                    val topCategorias = gastosPorCategoria.toList()
                        .sortedByDescending { it.second }
                        .take(5)

                    Triple(
                        Triple(cuentas, categoriasMap, movimientosList.sortedByDescending { it.date }),
                        Triple(balanceTotal, gasto, ingreso),
                        Pair(gastosPorMes, topCategorias)
                    )
                }.collect { result ->
                    val (baseData, totals, stats) = result
                    val (cuentas, categorias, movimientos) = baseData
                    val (balanceTotal, gastoTotal, ingresoTotal) = totals
                    val (gastosMensuales, categoriasMasGastadas) = stats

                    _uiState.update {
                        it.copy(
                            cuentas = cuentas,
                            categorias = categorias,
                            movimientos = movimientos,
                            balanceTotal = balanceTotal,
                            gastoTotal = gastoTotal,
                            ingresoTotal = ingresoTotal,
                            gastosMensuales = gastosMensuales,
                            categoriasMasGastadas = categoriasMasGastadas,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message, isLoading = false) }
            }
        }
    }

    fun setPeriodo(periodo: String) {
        _uiState.update { it.copy(periodoSeleccionado = periodo) }
    }

    fun deleteMovement(expense: Expense, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val cuenta = _uiState.value.cuentas.find { it.id == expense.accountId }
                movementRepository.deleteMovement(expense, cuenta)
                onSuccess()
            } catch (e: Exception) {
                onError("Error: ${e.message}")
            }
        }
    }

    fun signOut(onSuccess: () -> Unit) {
        authRepository.signOut()
        onSuccess()
    }

    fun getFilteredMovimientos(): List<Expense> {
        val movimientos = _uiState.value.movimientos
        val periodo = _uiState.value.periodoSeleccionado
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
                }.take(200)
            }
            else -> movimientos.take(50)
        }
    }
}
