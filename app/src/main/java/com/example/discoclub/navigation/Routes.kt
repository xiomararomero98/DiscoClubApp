package com.example.discoclub.navigation

// Clase sellada para rutas: evita "strings mágicos" y facilita refactors
sealed class Route(val path: String) { // Cada objeto representa una pantalla

    data object Splash : Route("splash")
    data object Home     : Route("home")     // Ruta Home
    data object Login    : Route("login")    // Ruta Login
    data object Register : Route("register") // Ruta Registro

    data object Admin    : Route("admin")    // Ruta Admin
    data object Pedido: Route("pedido")

}

