package com.example.discoclub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.discoclub.data.local.pedido.PedidoEntity
import com.example.discoclub.data.repository.PedidoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PedidosViewModel (private val repository: PedidoRepository): ViewModel(){
    private val _pedidosMesa = MutableStateFlow<List<PedidoEntity>>(emptyList())
    val pedidosMesa: StateFlow<List<PedidoEntity>> = _pedidosMesa

    fun loadPedidosMesa(mesaId: Long){
        viewModelScope.launch {
            repository.observePedidosByMesa(mesaId).collectLatest { pedidos -> _pedidosMesa.value = pedidos }
        }
    }

    fun addPedido(mesaId: Long, idProducto: Long, cantidad: Int, modificacion: String?){
        viewModelScope.launch {
            val pedido = PedidoEntity(
                mesaId = mesaId,
                idProducto = idProducto,
                cantidad = cantidad,
                modificacion = modificacion
            )
            repository.insertOne(pedido)
        }
    }
}