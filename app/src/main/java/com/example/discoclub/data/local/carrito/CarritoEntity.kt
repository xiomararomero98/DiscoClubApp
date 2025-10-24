package com.example.discoclub.data.local.carrito

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla "carrito" para ítems agregados por el usuario.
// Guardamos suficiente info para mostrar el carrito sin tener que joinear.
@Entity(tableName = "carrito")
data class CarritoEntity(
    @PrimaryKey(autoGenerate = true)       // Id autoincremental del ítem en el carrito
    val id: Long = 0L,

    val productoId: Long,                  // Id del producto (relación lógica)
    val nombreProducto: String,            // Nombre (cacheado para UI)
    val precioUnitario: Long,              // Precio unitario en CLP
    val imagenUrl: String? = null,         // Imagen (opcional)
    val cantidad: Int                      // Cantidad seleccionada
)
