package com.example.appmovimientos.ui.screens.categorias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovimientos.data.model.Category
import com.example.appmovimientos.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoriasViewModel(
    private val categoryRepository: CategoryRepository = CategoryRepository()
) : ViewModel() {

    data class CategoriasUiState(
        val categorias: List<Category> = emptyList(),
        val isLoading: Boolean = true,
        val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow(CategoriasUiState())
    val uiState: StateFlow<CategoriasUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                categoryRepository.getCategoriesFlow().collect { categorias ->
                    _uiState.update { it.copy(categorias = categorias, isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message, isLoading = false) }
            }
        }
    }

    fun deleteCategory(category: Category, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                categoryRepository.deleteCategory(category.id)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Error al eliminar categoría")
            }
        }
    }

    fun createCategory(
        nombre: String,
        tipo: String,
        icono: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (nombre.isBlank()) {
            onError("Por favor ingresa un nombre para la categoría")
            return
        }

        val category = Category(
            name = nombre,
            type = tipo,
            icon = icono,
            color = if (tipo == "Ingreso") "#4CAF50" else "#FF5722"
        )

        viewModelScope.launch {
            try {
                categoryRepository.createCategory(category)
                onSuccess()
            } catch (e: Exception) {
                onError("Error al crear la categoría: ${e.message}")
            }
        }
    }

    fun updateCategory(
        category: Category,
        nombre: String,
        tipo: String,
        icono: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (nombre.isBlank()) {
            onError("El nombre es obligatorio")
            return
        }

        val categoriaActualizada = category.copy(
            name = nombre,
            type = tipo,
            icon = icono,
            color = if (tipo == "Ingreso") "#4CAF50" else "#FF5722"
        )

        viewModelScope.launch {
            try {
                categoryRepository.updateCategory(categoriaActualizada)
                onSuccess()
            } catch (e: Exception) {
                onError("Error al actualizar: ${e.message}")
            }
        }
    }
}
