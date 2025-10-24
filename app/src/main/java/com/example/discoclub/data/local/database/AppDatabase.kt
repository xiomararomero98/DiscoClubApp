package com.example.discoclub.data.local.database


import android.content.Context                                  // Contexto para construir DB
import androidx.room.Database                                   // Anotación @Database
import androidx.room.Room                                       // Builder de DB
import androidx.room.RoomDatabase                               // Clase base de DB
import androidx.sqlite.db.SupportSQLiteDatabase                 // Tipo del callback onCreate
import com.example.discoclub.data.local.user.UserDao         // Import del DAO de usuario
import com.example.discoclub.data.local.user.UserEntity      // Import de la entidad de usuario
import kotlinx.coroutines.CoroutineScope                        // Para corrutinas en callback
import kotlinx.coroutines.Dispatchers                           // Dispatcher IO
import kotlinx.coroutines.launch                                // Lanzar corrutina

// @Database registra entidades y versión del esquema.
// version = 1: como es primera inclusión con teléfono, partimos en 1.
@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = true // Mantener true para inspección de esquema (útil en educación)
)
abstract class AppDatabase : RoomDatabase() {

    // Exponemos el DAO de usuarios
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null              // Instancia singleton
        private const val DB_NAME = "ui_navegacion.db"         // Nombre del archivo .db

        // Obtiene la instancia única de la base
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // Construimos la DB con callback de precarga
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    // Callback para ejecutar cuando la DB se crea por primera vez
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Lanzamos una corrutina en IO para insertar datos iniciales
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getInstance(context).userDao()

                                // Precarga de usuarios (incluye teléfono)
                                // Reemplaza aquí por los mismitos datos que usas en Login/Register.
                                val seed = listOf(
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

                                // Inserta seed sólo si la tabla está vacía
                                if (dao.count() == 0) {
                                    seed.forEach { dao.insert(it) }
                                }
                            }
                        }
                    })
                    // En entorno educativo, si cambias versión sin migraciones, destruye y recrea.
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance                             // Guarda la instancia
                instance                                        // Devuelve la instancia
            }
        }
    }
}