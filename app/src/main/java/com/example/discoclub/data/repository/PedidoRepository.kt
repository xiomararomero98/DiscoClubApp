package com.example.discoclub.data.repository

import com.example.discoclub.data.local.pedido.PedidoDao
import com.example.discoclub.data.local.pedido.PedidoEntity
import kotlinx.coroutines.flow.Flow

class PedidoRepository(private val pedidoDao: PedidoDao){
    fun observePedidos(): Flow<List<PedidoEntity>> = pedidoDao.getAll()
    fun observePedidosByMesa(mesaId: Long): Flow<List<PedidoEntity>> = pedidoDao.getPedidosByMesa(mesaId)

    suspend fun insertAll(list: List<PedidoEntity>) = pedidoDao.insertAll(list)
    suspend fun insertOne(pedido: PedidoEntity): Long = pedidoDao.insertOne(pedido)
    suspend fun deleteById(id: Long) = pedidoDao.deleteById(id)
    suspend fun clearAll() = pedidoDao.clear()
}