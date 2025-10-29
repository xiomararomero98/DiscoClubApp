package com.example.discoclub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.discoclub.data.local.carrito.CarritoEntity
import com.example.discoclub.data.repository.CarritoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(
    private val repo: CarritoRepository
) : ViewModel() {

    val items: StateFlow<List<CarritoEntity>> =
        repo.observeCart().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val totalCLP: StateFlow<Long> =
        repo.observeTotalCLP().stateIn(viewModelScope, SharingStarted.Eagerly, 0L)

    // ===== ACCIONES =====
    fun add(
        productoId: Long,
        nombreProducto: String,
        precioUnitario: Long,
        imagenUrl: String? = null
    ) = viewModelScope.launch {
        repo.addOrIncrement(productoId, nombreProducto, precioUnitario, imagenUrl)
    }

    fun updateCantidad(productoId: Long, nueva: Int) = viewModelScope.launch {
        repo.updateCantidad(productoId, nueva)
    }

    fun decrementOrRemove(productoId: Long) = viewModelScope.launch {
        repo.decrementOrRemove(productoId)
    }

    fun remove(productoId: Long) = viewModelScope.launch {
        repo.remove(productoId)
    }

    fun checkout(onDone: (() -> Unit)? = null) = viewModelScope.launch {
        repo.checkout()
        onDone?.invoke()
    }
}
