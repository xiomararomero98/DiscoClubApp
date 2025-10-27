package com.example.discoclub.data.local.pedido

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface PedidoDao{
    //Insert de varios (Lista)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<PedidoEntity>)

    //Insert de uno
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne()
}