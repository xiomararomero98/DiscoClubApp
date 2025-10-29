package com.example.discoclub.ui.screen

import android.R.color.white
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.discoclub.data.local.productos.ProductosEntity
import com.example.discoclub.ui.utils.toCLP
import com.example.discoclub.ui.viewmodel.ProductsViewModel
import com.example.discoclub.ui.viewmodel.CartViewModel
import kotlinx.coroutines.launch

@Composable
fun ProductsScreen(
    vm: ProductsViewModel,
    carritoVm: CartViewModel,          //  PASA el VM del carrito
    onGoCart: () -> Unit,
    onGoProfile: () -> Unit
) {
    val bg = MaterialTheme.colorScheme.surfaceVariant
    val uiState by vm.uiState.collectAsState()

    // ✅ Snackbar
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
                    text = "Productos disponibles",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = vm::onSearchQueryChange,
                    label = { Text("Buscar producto") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                    uiState.productos.isEmpty() -> {
                        Text("No hay productos disponibles.", style = MaterialTheme.typography.bodyLarge)
                    }
                    else -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(uiState.productos, key = { it.id }) { producto ->
                                ProductoCard(
                                    producto = producto,
                                    onAddToCart = { p ->
                                        carritoVm.add(
                                            productoId     = p.id,        // si tu PK se llama distinto, cámbialo aquí
                                            nombreProducto = p.nombre,
                                            precioUnitario = p.precio,
                                            imagenUrl      = p.imagenUrl
                                        )
                                        // ✅ Mostrar Snackbar de confirmación
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = "Producto agregado correctamente",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                )
                            }
                        }
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
}

@SuppressLint("DiscouragedApi", "LocalContextResourcesRead")
@Composable
private fun ProductoCard(
    producto: ProductosEntity,
    onAddToCart: (ProductosEntity) -> Unit
) {
    val context = LocalContext.current
    val imagenesId = remember(producto.imagenUrl) {
        producto.imagenUrl?.let {
            context.resources.getIdentifier(it, "drawable", context.packageName)
        } ?: 0
    }
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (imagenesId != 0) {
                Image(
                    painter = painterResource(id = imagenesId),
                    contentDescription = producto.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sin imagen", color = Color.White)
                }
            }
            Spacer(Modifier.height(8.dp))

            Text(
                producto.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(producto.descripcion ?: "", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text("Precio: ${producto.precio.toCLP()}", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { onAddToCart(producto) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Agregar al carrito") }
        }
    }
}
