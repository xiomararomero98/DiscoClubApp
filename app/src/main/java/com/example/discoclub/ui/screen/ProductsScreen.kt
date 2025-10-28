package com.example.discoclub.ui.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.discoclub.data.local.productos.ProductosEntity
import com.example.discoclub.ui.utils.toCLP
import com.example.discoclub.ui.viewmodel.ProductsViewModel

@Composable
fun ProductsScreen(
    vm: ProductsViewModel,            // <-- AGREGA ESTE PARÁMETRO

    onGoCart: () -> Unit,
    onGoProfile: () -> Unit
) {
    val bg = MaterialTheme.colorScheme.surfaceVariant

    // Productos de ejemplo (luego los traerás desde el ViewModel)
    val productos = remember {
        listOf(
            ProductosEntity(nombre = "Pisco Sour", precio = 4500L, stock = 20, descripcion = "Trago tradicional chileno"),
            ProductosEntity(nombre = "Vodka + Energética", precio = 5500L, stock = 15, descripcion = "Combinado clásico"),
            ProductosEntity(nombre = "Bebida Energética", precio = 2500L, stock = 40, descripcion = "Lata 250ml")
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column {
            Text(
                text = "Productos disponibles",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(productos) { producto ->
                    ProductoCard(producto = producto)
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onGoCart) { Text("Ir al Carrito") }
                OutlinedButton(onClick = onGoProfile) { Text("Perfil") }
            }
        }
    }
}

@Composable
fun ProductoCard(producto: ProductosEntity) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(producto.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(producto.descripcion ?: "", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text("Precio: ${producto.precio.toCLP()}", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(8.dp))
            Button(onClick = { /* TODO agregar al carrito */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Agregar al carrito")
            }
        }
    }
}
