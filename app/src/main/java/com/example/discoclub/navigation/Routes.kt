package com.example.discoclub.navigation

sealed class Route(val path: String) {
    data object Home    : Route("Home")
    data object Login   : Route("Login") //Ruta para el login
    data object Register: Route ("Register")
}