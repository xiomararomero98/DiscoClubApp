package com.example.discoclub.data.local.carrito

import androidx.room.*                     // Dao/Query/Insert/Update/Delete
import kotlinx.coroutines.flow.Flow

// DAO del carrito: operaciones CRUD y observación en tiempo real.
@Dao
interface CarritoDao {
    @Query("SELECT * FROM carrito ORDER BY nombreProducto ASC")
    fun observeCart(): Flow<List<CarritoEntity>>

    @Query("SELECT * FROM carrito ORDER BY nombreProducto ASC")
    suspend fun getAllOnce(): List<CarritoEntity>

    @Query("SELECT * FROM carrito WHERE productoId = :productoId LIMIT 1")
    suspend fun getByProductoId(productoId: Long): CarritoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CarritoEntity): Long

    @Update suspend fun update(item: CarritoEntity)
    @Delete suspend fun delete(item: CarritoEntity)

    @Query("DELETE FROM carrito WHERE productoId = :productoId")
    suspend fun removeByProductoId(productoId: Long)

    @Query("DELETE FROM carrito")
    suspend fun clear()

    @Query("SELECT COALESCE(SUM(cantidad), 0) FROM carrito")
    fun observeTotalItems(): Flow<Int>

    @Query("SELECT COALESCE(SUM(precioUnitario * cantidad), 0) FROM carrito")
    fun observeTotalCLP(): Flow<Long>
}
