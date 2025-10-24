package com.example.discoclub.data.local.user

import androidx.room.Entity
import androidx.room.PrimaryKey


// @Entity declara una tabla SQLite manejada por Room.
// tableName = "users" define el nombre exacto de la tabla.

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) //clave primaria autoincremental
    val id: Long =0L,
    val name: String,
    val email: String,
    val phone: String,
    val password: String
)