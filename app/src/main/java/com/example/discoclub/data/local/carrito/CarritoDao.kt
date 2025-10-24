package com.example.discoclub.data.local.carrito

import androidx.room.*                     // Dao/Query/Insert/Update/Delete
import kotlinx.coroutines.flow.Flow

// DAO del carrito: operaciones CRUD y observación en tiempo real.
@Dao
interface CarritoDao {

    // Observa todo el carrito ordenado por nombre
    @Query("SELECT * FROM carrito ORDER BY nombreProducto ASC")
    fun observeCart(): Flow<List<CarritoEntity>>

    // Obtiene una vez (no reactivo)
    @Query("SELECT * FROM carrito ORDER BY nombreProducto ASC")
    suspend fun getAllOnce(): List<CarritoEntity>

    // Busca ítem por productoId (para sumar cantidad)
    @Query("SELECT * FROM carrito WHERE productoId = :productoId LIMIT 1")
    suspend fun getByProductoId(productoId: Long): CarritoEntity?

    // Inserta o reemplaza ítem
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CarritoEntity): Long

    // Actualiza ítem
    @Update
    suspend fun update(item: CarritoEntity)

    // Elimina ítem específico
    @Delete
    suspend fun delete(item: CarritoEntity)

    // Elimina por productoId
    @Query("DELETE FROM carrito WHERE productoId = :productoId")
    suspend fun removeByProductoId(productoId: Long)

    // Vacía carrito
    @Query("DELETE FROM carrito")
    suspend fun clear()

    // Cantidad total de ítems (suma de cantidades)
    @Query("SELECT COALESCE(SUM(cantidad), 0) FROM carrito")
    fun observeTotalItems(): Flow<Int>

    // Total en CLP (precio * cantidad)
    @Query("SELECT COALESCE(SUM(precioUnitario * cantidad), 0) FROM carrito")
    fun observeTotalCLP(): Flow<Long>
}
