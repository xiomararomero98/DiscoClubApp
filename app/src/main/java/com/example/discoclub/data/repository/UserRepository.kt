package com.example.discoclub.data.repository

import com.example.discoclub.data.local.user.UserDao       // DAO de usuario
import com.example.discoclub.data.local.user.UserEntity    // Entidad de usuario
import kotlinx.coroutines.flow.Flow

// Repositorio: orquesta reglas de negocio para login/registro sobre el DAO.
class UserRepository(
    private val userDao: UserDao // Inyección del DAO
) {

    // Login: busca por email y valida contraseña
    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.getByEmail(email)                         // Busca usuario
        return if (user != null && user.password == password) {      // Verifica pass
            Result.success(user)                                     // Éxito
        } else {
            Result.failure(IllegalArgumentException("Credenciales inválidas")) // Error
        }
    }

    // Registro: valida no duplicado y crea nuevo usuario (con teléfono)
    suspend fun register(name: String, email: String, phone: String, password: String): Result<Long> {
        val exists = userDao.getByEmail(email) != null               // ¿Correo ya usado?
        if (exists) {
            return Result.failure(IllegalStateException("El correo ya está registrado"))
        }
        val id = userDao.insert(                                     // Inserta nuevo
            UserEntity(
                name = name,
                email = email,
                phone = phone,                                       // Teléfono incluido
                password = password
            )
        )
        return Result.success(id)                                    // Devuelve ID generado
    }

    //  Obtener todos los usuarios registrados (para AdminPerfilesScreen)
    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()

    //Funciones para editar/eliminar usuarios
    // Obtener usuario por ID (para pantalla de edición)
    suspend fun getUserById(id: Int): UserEntity? {
        return userDao.getById(id)
    }

    // Actualizar usuario existente
    suspend fun updateUser(user: UserEntity) {
        userDao.update(user)
    }

    // Eliminar usuario
    suspend fun deleteUser(user: UserEntity) {
        userDao.delete(user)
    }
}