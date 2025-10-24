package com.example.discoclub.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.discoclub.data.local.user.UserDao
import com.example.discoclub.data.local.user.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema =  true
)
abstract class AppDatabase : RoomDatabase(){
    //exponemos el DAO de usuarios

    abstract fun userDao(): UserDao

    companion object{
        @Volatile
        private var INSTANCE : AppDatabase? = null //instancia singleton
        private const val DB_NAME = "DiscoClub.db"
        //obtiene la instancia unica de la base
        fun getInstance(context: Context): AppDatabase{
            return INSTANCE ?: synchronized(this){
                //Construimos la DB con callback de precarga
                val  instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                 //callback para ejecutar cuando la DB se crea por primera vez
                    .addCallback(object : RoomDatabase.Callback(){
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            //lanzamos una corrutina en IO para insertar datos iniciales
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getInstance(context).userDao()

                                //precarga de usuarios (incluye el telefono)
                                //reemplaza aqui por los mismos datos que usas en login/registro
                                val seed = listOf(
                                    UserEntity(
                                        name = "Admin",
                                        email = "admin@duoc.cl",
                                        phone = "+56928683281",
                                        password = "Admin123!"
                                    ),
                                    UserEntity(
                                        name = "Xiomara Romero",
                                        email = "xiomara@duoc.cl",
                                        phone = "+56922222222",
                                        password = "123456"
                                    )
                                )

                                //inserta seed solo si la tabla esta vacia
                                if (dao.count() == 0){
                                    seed.forEach { dao.insert(it) }
                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}