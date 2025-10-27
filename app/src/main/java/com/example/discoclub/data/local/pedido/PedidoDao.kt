package com.example.discoclub.data.local.pedido

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

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

    @Delete
    suspend fun delete(pedido: PedidoEntity)
}