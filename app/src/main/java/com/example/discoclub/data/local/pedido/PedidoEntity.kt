package com.example.discoclub.data.local.pedido

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("Pedido")
data class PedidoEntity(
    @PrimaryKey(true)
    val id: Long = 0L,

    val mesaId: Long = 0L,
    val idProducto: Long = 0L,
    val cantidad: Int,
    val modificacion: String? = null
)