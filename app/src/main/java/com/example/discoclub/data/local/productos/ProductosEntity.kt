package com.example.discoclub.data.local.productos

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "productos",
    indices = [Index(value = ["nombre"], unique = false)])
data class ProductosEntity(
    @PrimaryKey(autoGenerate = true)       // Clave primaria autoincremental
    val id: Long = 0L,

    val nombre: String,                    // Nombre del producto
    val descripcion: String? = null,       // Descripción (opcional)
    val precio: Long,                      // Precio unitario en CLP (usa Long)
    val stock: Int,                        // Stock disponible
    val imagenUrl: String? = null          // URL de imagen (opcional)


)