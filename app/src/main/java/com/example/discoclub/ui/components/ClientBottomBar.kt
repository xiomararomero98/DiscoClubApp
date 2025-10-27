package com.example.discoclub.ui.components
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.discoclub.navigation.Route

@Composable
fun ClientBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        // --- Productos ---
        NavigationBarItem(
            selected = currentRoute == Route.Client.Products.path,
            onClick = { onNavigate(Route.Client.Products.path) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Productos") },
            label = { Text("Productos") }
        )

        // --- Carrito ---
        NavigationBarItem(
            selected = currentRoute == Route.Client.Cart.path,
            onClick = { onNavigate(Route.Client.Cart.path) },
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito") },
            label = { Text("Carrito") }
        )

        // --- Perfil ---
        NavigationBarItem(
            selected = currentRoute == Route.Client.Profile.path,
            onClick = { onNavigate(Route.Client.Profile.path) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") }
        )
    }
}
