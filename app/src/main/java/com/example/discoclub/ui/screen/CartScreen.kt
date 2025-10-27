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
import com.example.discoclub.data.local.carrito.CarritoEntity
import com.example.discoclub.ui.utils.toCLP

@Composable
fun CartScreen(
    onGoProducts: () -> Unit,
    onGoProfile: () -> Unit
) {
    val bg = MaterialTheme.colorScheme.surfaceVariant

    // Datos de ejemplo (luego se conectan al ViewModel)
    val carrito = remember {
        listOf(
            CarritoEntity(productoId = 1L, nombreProducto = "Pisco Sour", precioUnitario = 4500L, cantidad = 2),
            CarritoEntity(productoId = 2L, nombreProducto = "Vodka + Energética", precioUnitario = 5500L, cantidad = 1)
        )
    }

    val total = carrito.sumOf { it.precioUnitario * it.cantidad }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column {
            Text(
                text = "Tu carrito",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(12.dp))

            if (carrito.isEmpty()) {
                Text("El carrito está vacío", style = MaterialTheme.typography.bodyLarge)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(carrito) { item ->
                        CarritoItemCard(item)
                    }
                }

                Spacer(Modifier.height(16.dp))
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Total a pagar: ${total.toCLP()}", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { /* TODO proceso de compra */ }, modifier = Modifier.fillMaxWidth()) {
                            Text("Finalizar compra")
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onGoProducts) { Text("Volver a productos") }
                OutlinedButton(onClick = onGoProfile) { Text("Perfil") }
            }
        }
    }
}

@Composable
fun CarritoItemCard(item: CarritoEntity) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(item.nombreProducto, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Cantidad: ${item.cantidad}")
            Text("Precio unitario: ${item.precioUnitario.toCLP()}")
            Spacer(Modifier.height(8.dp))
            Text("Subtotal: ${(item.precioUnitario * item.cantidad).toCLP()}", fontWeight = FontWeight.Medium)
        }
    }
}
