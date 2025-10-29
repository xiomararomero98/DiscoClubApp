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
import com.example.discoclub.ui.viewmodel.CartViewModel
import kotlinx.coroutines.launch

@Composable
fun CartScreen(
    onGoProducts: () -> Unit,
    onGoProfile: () -> Unit,
    vm: CartViewModel
) {
    val bg = MaterialTheme.colorScheme.surfaceVariant
    val items by vm.items.collectAsState()
    val total by vm.totalCLP.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = bg
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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

                if (items.isEmpty()) {
                    Text("El carrito está vacío", style = MaterialTheme.typography.bodyLarge)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(items, key = { it.productoId }) { item ->
                            CarritoItemCard(
                                item = item,
                                onDecrement = {
                                    val nueva = item.cantidad - 1
                                    vm.updateCantidad(item.productoId, nueva) // o vm.decrementOrRemove(item.productoId)
                                },
                                onIncrement = {
                                    val nueva = item.cantidad + 1
                                    vm.updateCantidad(item.productoId, nueva) // o vm.add(...)
                                },
                                onRemove = { vm.remove(item.productoId) }
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Total a pagar: ${total.toCLP()}",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    scope.launch {
                                        vm.checkout()
                                        snackbarHostState.showSnackbar(
                                            message = "Compra finalizada correctamente",
                                            duration = SnackbarDuration.Short
                                        )
                                        onGoProducts()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Finalizar compra") }
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
}

@Composable
private fun CarritoItemCard(
    item: CarritoEntity,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    onRemove: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                item.nombreProducto,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text("Precio unitario: ${item.precioUnitario.toCLP()}")
            Text("Cantidad: ${item.cantidad}")
            Spacer(Modifier.height(6.dp))
            Text(
                "Subtotal: ${(item.precioUnitario * item.cantidad).toCLP()}",
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onDecrement, enabled = item.cantidad > 0) { Text("-") }
                OutlinedButton(onClick = onIncrement) { Text("+") }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onRemove) { Text("Quitar") }
            }
        }
    }
}
