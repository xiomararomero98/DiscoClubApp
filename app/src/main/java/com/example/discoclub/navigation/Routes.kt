package com.example.discoclub.navigation

// Clase sellada para rutas: evita "strings mágicos" y facilita refactors
sealed class Route(val path: String) { // Cada objeto representa una pantalla
    data object Home : Route("home")     // Ruta Home
    data object Login : Route("login")    // Ruta Login
    data object Register : Route("register") // Ruta Registro


    // --- Rutas privadas: vistas del CLIENTE ---
    sealed class Client(path: String) : Route(path) {
        data object Root : Client("client")
        data object Products : Client("client/products")
        data object Cart : Client("client/cart")
        data object Profile : Client("client/profile")
    }

}