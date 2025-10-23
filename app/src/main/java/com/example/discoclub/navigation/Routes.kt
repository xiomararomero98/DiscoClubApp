package com.example.discoclub.navigation

sealed class Route(val path: String) {
    data object Login   : Route("Login") //Ruta para el login
}