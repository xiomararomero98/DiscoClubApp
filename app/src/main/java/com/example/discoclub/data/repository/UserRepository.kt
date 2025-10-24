package com.example.discoclub.data.repository

import com.example.discoclub.data.local.user.UserDao
import com.example.discoclub.data.local.user.UserEntity

class UserRepository (
    private val userDao: UserDao //inyeccion del DAO
){
    //login: busca por email y valida contraseña
    suspend fun login(email: String, password: String): Result<UserEntity>{
        val user = userDao.getByEmail(email)
        return if (user != null && user.password == password){
            Result.success(user)
        }else{
            Result.failure(IllegalArgumentException("Credenciales inválidas"))
        }
    }
    //Registro:valida no duplicado y crea nuevo usuario (con telefono)

    suspend fun register(name: String, email: String, phone: String, password: String) : Result<Long>{
        val exists = userDao.getByEmail(email) !=null
        if (exists){
            return Result.failure(IllegalStateException("El correo ya está registrado"))
        }
        val id = userDao.insert(
            UserEntity(
                name = name,
                email =email,
                phone = phone,
                password = password
            )
        )
        return Result.success(id)
    }
}