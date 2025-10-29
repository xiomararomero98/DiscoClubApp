package com.example.discoclub.data.local.carrito

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "carrito",
    indices = [Index(value = ["productoId"], unique = true)]
)
data class CarritoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val productoId: Long,
    val nombreProducto: String,
    val precioUnitario: Long,
    val imagenUrl: String? = null,
    val cantidad: Int
)
