package com.example.discoclub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.discoclub.data.local.productos.ProductosEntity
import com.example.discoclub.data.repository.ProductosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class ProductsUiState(
    val productos: List<ProductosEntity> = emptyList(),
    val query: String = "",
    val isLoading: Boolean = false
)

class ProductsViewModel(
    private val repository: ProductosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState: StateFlow<ProductsUiState> = _uiState

    init {
        loadProductos()
    }

    fun onSearchQueryChange(value: String) {
        _uiState.value = _uiState.value.copy(query = value)
        searchProductos(value)
    }

    private fun loadProductos() {
        viewModelScope.launch {
            repository.observeProductos().collectLatest { list ->
                _uiState.value = _uiState.value.copy(productos = list, isLoading = false)
            }
        }
    }

    private fun searchProductos(query: String) {
        viewModelScope.launch {
            repository.searchByName(query).collectLatest { list ->
                _uiState.value = _uiState.value.copy(productos = list, isLoading = false)
            }
        }
    }
}
