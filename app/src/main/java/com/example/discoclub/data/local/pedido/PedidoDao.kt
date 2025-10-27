package com.example.discoclub.data.local.pedido

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao{
    //Insert de varios (Lista)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<PedidoEntity>)

    //Insert de uno por ID
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(pedido: PedidoEntity): Long

    @Update
    suspend fun updateOne(pedido: PedidoEntity)

    @Query("SELECT * FROM Pedido")
    fun getAll(): Flow<List<PedidoEntity>>

    @Query("SELECT * FROM Pedido WHERE id = :id")
    suspend fun getById(id: Long): PedidoEntity?

    @Query("DELETE FROM Pedido WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("DELETE FROM Pedido")
    suspend fun clear()

    @Query("SELECT * FROM Pedido WHERE mesaId = :mesaId")
    fun getPedidosByMesa(mesaId: Long): Flow<List<PedidoEntity>>
}