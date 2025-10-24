package com.example.discoclub.data.local.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    //inserta un usuario. ABORT si hay conflicto de PK (no de email; ese lo controlamos a mano).

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user:UserEntity): Long

    //devuelve un usuario por email o null si no existe

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    @Query("SELECT COUNT (*) FROM users")
    suspend fun count(): Int

    //lista completa util para debug/administracion
    @Query("SELECT * FROM users ORDER BY id ASC")
    suspend fun getAll(): List<UserEntity>

}