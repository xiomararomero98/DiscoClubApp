package com.example.discoclub.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.discoclub.data.local.carrito.CarritoEntity
import com.example.discoclub.data.repository.CarritoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class CartUiState(
    val items: List<CarritoEntity> = emptyList(),
    val totalItems: Int = 0,
    val totalCLP: Long = 0L
)

class CartViewModel(
    private val repository: CarritoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState

    init {
        observeCart()
    }

    private fun observeCart() {
        viewModelScope.launch {
            combine(
                repository.observeCart(),
                repository.observeTotalItems(),
                repository.observeTotalCLP()
            ) { items, totalItems, totalCLP ->
                CartUiState(items, totalItems, totalCLP)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun addItem(
        productoId: Long,
        nombre: String,
        precio: Long,
        imagenUrl: String? = null
    ) = viewModelScope.launch {
        repository.addOrIncrement(productoId, nombre, precio, imagenUrl)
    }

    fun updateCantidad(productoId: Long, nuevaCantidad: Int) = viewModelScope.launch {
        repository.updateCantidad(productoId, nuevaCantidad)
    }

    fun removeItem(productoId: Long) = viewModelScope.launch {
        repository.remove(productoId)
    }

    fun clearCart() = viewModelScope.launch {
        repository.clear()
    }
}
