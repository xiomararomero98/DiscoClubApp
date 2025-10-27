package com.example.discoclub.data.repository


import com.example.discoclub.data.local.carrito.CarritoDao
import com.example.discoclub.data.local.carrito.CarritoEntity
import kotlinx.coroutines.flow.Flow

// Repositorio del carrito: suma/resta y totales en CLP.
class CarritoRepository(
    private val carritoDao: CarritoDao
) {
    fun observeCart(): Flow<List<CarritoEntity>> = carritoDao.observeCart()
    fun observeTotalItems(): Flow<Int> = carritoDao.observeTotalItems()
    fun observeTotalCLP(): Flow<Long> = carritoDao.observeTotalCLP()

    suspend fun addOrIncrement(
        productoId: Long,
        nombre: String,
        precioUnitario: Long,
        imagenUrl: String? = null,
        cantidad: Int = 1
    ) {
        val current = carritoDao.getByProductoId(productoId)
        val nuevaCantidad = (current?.cantidad ?: 0) + cantidad
        val entity = CarritoEntity(
            id = current?.id ?: 0L,
            productoId = productoId,
            nombreProducto = nombre,
            precioUnitario = precioUnitario,
            imagenUrl = imagenUrl,
            cantidad = nuevaCantidad
        )
        carritoDao.upsert(entity)
    }

    suspend fun updateCantidad(productoId: Long, nueva: Int) {
        val current = carritoDao.getByProductoId(productoId) ?: return
        if (nueva <= 0) carritoDao.removeByProductoId(productoId)
        else carritoDao.upsert(current.copy(cantidad = nueva))
    }

    suspend fun remove(productoId: Long) = carritoDao.removeByProductoId(productoId)
    suspend fun clear() = carritoDao.clear()
}
