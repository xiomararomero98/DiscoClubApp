package com.example.discoclub.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.discoclub.data.local.user.UserDao
import com.example.discoclub.data.local.user.UserEntity
import com.example.discoclub.data.local.productos.ProductosDao
import com.example.discoclub.data.local.productos.ProductosEntity
import com.example.discoclub.data.local.carrito.CarritoDao
import com.example.discoclub.data.local.carrito.CarritoEntity
import com.example.discoclub.data.local.pedido.PedidoDao
import com.example.discoclub.data.local.pedido.PedidoEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Base de datos principal del proyecto DiscoClub.
 * Usa Room como ORM para manejar entidades locales.
 *
 * Contiene 3 tablas:
 * - users
 * - productos
 * - carrito
 */
@Database(
    entities = [
        UserEntity::class,
        ProductosEntity::class,
        CarritoEntity::class,
        PedidoEntity::class
    ],
    version = 2, // Incrementar versión cuando cambien entidades
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    // DAOs expuestos
    abstract fun userDao(): UserDao
    abstract fun productosDao(): ProductosDao
    abstract fun carritoDao(): CarritoDao

    abstract fun pedidoDao(): PedidoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private const val DB_NAME = "DiscoClub.db" // Archivo físico de la base de datos

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Cargar datos iniciales solo una vez, al crear la base
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { database ->
                                    val userDao = database.userDao()
                                    val productosDao = database.productosDao()
                                    val pedidoDao = database.pedidoDao()

                                    // Precarga de usuarios
                                    if (userDao.count() == 0) {
                                        val usuariosSeed = listOf(
                                            UserEntity(
                                                name = "Admin",
                                                email = "admin@duoc.cl",
                                                phone = "+56911111111",
                                                password = "Admin123!"
                                            ),
                                            UserEntity(
                                                name = "Víctor Rosendo",
                                                email = "victor@duoc.cl",
                                                phone = "+56922222222",
                                                password = "123456"
                                            )
                                        )
                                        usuariosSeed.forEach { userDao.insert(it) }
                                    }

                                    // Precarga de productos
                                    if (productosDao.count() == 0) {
                                        val productosSeed = listOf(
                                            ProductosEntity(
                                                nombre = "Pisco Sour",
                                                descripcion = "Trago tradicional chileno 350ml",
                                                precio = 4500L,
                                                stock = 20,
                                                imagenUrl = null
                                            ),
                                            ProductosEntity(
                                                nombre = "Bebida Energética",
                                                descripcion = "Lata 250ml",
                                                precio = 2500L,
                                                stock = 40,
                                                imagenUrl = null
                                            ),
                                            ProductosEntity(
                                                nombre = "Vodka + Energética",
                                                descripcion = "Combinado clásico 500ml",
                                                precio = 5500L,
                                                stock = 15,
                                                imagenUrl = null
                                            )
                                        )
                                        productosSeed.forEach { productosDao.upsert(it) }
                                    }

                                    if (pedidoDao.count() == 0){
                                        val pedidoSeed = PedidoEntity(
                                                mesaId = 1,
                                                idProducto = 2,
                                                cantidad = 3
                                        )
                                        pedidoDao.insertOne(pedidoSeed)
                                    }
                                }
                            }
                        }
                    })
                    // En desarrollo educativo: si cambian las entidades, recrea la BD.
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
