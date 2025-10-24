package com.example.discoclub.data.repository

import com.example.discoclub.data.local.productos.ProductosDao
import com.example.discoclub.data.local.productos.ProductosEntity
import kotlinx.coroutines.flow.Flow
// Repositorio de productos: orquesta reglas entre Room y (más adelante) API.

class ProductosRepository (
    private val productosDao: ProductosDao
){
    //observa todos los productos (UI reactiva)
    fun observeProductos(): Flow<List<ProductosEntity>> = productosDao.observeAll()

    // Carga puntual (no reactiva)
    suspend fun getAllOnce(): List<ProductosEntity> = productosDao.getAllOnce()

    // Búsqueda por nombre
    fun searchByName(query: String): Flow<List<ProductosEntity>> = productosDao.searchByName(query)

    // Upsert (por si luego sincronizas con servidor)
    suspend fun upsertAll(list: List<ProductosEntity>) = productosDao.upsertAll(list)




}