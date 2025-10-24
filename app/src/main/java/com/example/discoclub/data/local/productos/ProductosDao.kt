package com.example.discoclub.data.local.productos

import androidx.room.Dao                   // Marca interfaz como DAO
import androidx.room.Insert                // Insertar filas
import androidx.room.OnConflictStrategy
import androidx.room.Query                 // Consultas SQL
import androidx.room.Update                // Actualizaciones
import androidx.room.Delete                // Eliminaciones
import kotlinx.coroutines.flow.Flow        // Observación reactiva

// @Dao: operaciones CRUD para la tabla "productos".
@Dao
interface ProductosDao {

    // Inserta o actualiza (por id) una lista completa
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(list: List<ProductosEntity>)

    // Inserta o actualiza (por id) un solo producto
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(producto: ProductosEntity): Long

    // Actualiza un producto existente
    @Update
    suspend fun update(producto: ProductosEntity)

    // Elimina un producto
    @Delete
    suspend fun delete(producto: ProductosEntity)

    // Observa todos los productos (ordenados por nombre ASC)
    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun observeAll(): Flow<List<ProductosEntity>>

    // Obtiene todos (suspend) – útil para operaciones puntuales
    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    suspend fun getAllOnce(): List<ProductosEntity>

    // Busca por id
    @Query("SELECT * FROM productos WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ProductosEntity?

    // Búsqueda por nombre
    @Query("SELECT * FROM productos WHERE nombre LIKE '%' || :query || '%' ORDER BY nombre ASC")
    fun searchByName(query: String): Flow<List<ProductosEntity>>

    // Cuenta total de productos
    @Query("SELECT COUNT(*) FROM productos")
    suspend fun count(): Int

    // Limpia tabla
    @Query("DELETE FROM productos")
    suspend fun clear()
}
