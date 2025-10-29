package com.example.discoclub.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.discoclub.data.local.productos.ProductosEntity
import com.example.discoclub.ui.utils.toCLP
import com.example.discoclub.ui.viewmodel.ProductsViewModel
import com.example.discoclub.ui.viewmodel.CartViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@Composable
fun HomeScreen(
    vm: ProductsViewModel,
    carritoVm: CartViewModel,
    onSeeAll: () -> Unit,
    onGoCart: () -> Unit,
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit
) {
    val ui by vm.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val destacados = remember(ui.productos) { ui.productos.take(6) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .verticalScroll(rememberScrollState())
        ) {
            // ===================== HERO =====================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Text(
                        "DiscoClub",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Tragos, música y noche 🔥",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = onSeeAll) { Text("Ver productos") }
                        OutlinedButton(onClick = onGoCart) { Text("Ver carrito") }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ===================== HAPPY HOUR =====================
            HappyHourBanner()

            Spacer(Modifier.height(20.dp))

            // ===================== CHIPS RÁPIDOS =====================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(onClick = onSeeAll, label = { Text("Promos") })
                AssistChip(onClick = onSeeAll, label = { Text("Shots") })
                AssistChip(onClick = onSeeAll, label = { Text("Clásicos") })
                AssistChip(onClick = onSeeAll, label = { Text("Sin alcohol") })
            }

            Spacer(Modifier.height(12.dp))

            // ===================== DESTACADOS =====================
            SectionTitle(
                title = "Destacados",
                actionText = "Ver todo",
                onAction = onSeeAll
            )

            if (ui.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(8.dp))
            }

            if (destacados.isEmpty()) {
                Text(
                    "Aún no hay productos cargados.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(destacados, key = { it.id }) { p ->
                        ProductoCardHome(
                            producto = p,
                            onAdd = {
                                carritoVm.add(
                                    productoId = p.id,
                                    nombreProducto = p.nombre,
                                    precioUnitario = p.precio,
                                    imagenUrl = p.imagenUrl
                                )
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Producto agregado al carrito")
                                }
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ===================== CTA LOGIN/REGISTRO =====================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onGoLogin,
                    modifier = Modifier.weight(1f)
                ) { Text("Iniciar sesión") }
                OutlinedButton(
                    onClick = onGoRegister,
                    modifier = Modifier.weight(1f)
                ) { Text("Crear cuenta") }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        if (actionText != null && onAction != null) {
            TextButton(onClick = onAction) { Text(actionText) }
        }
    }
}

@Composable
private fun ProductoCardHome(
    producto: ProductosEntity,
    onAdd: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .width(240.dp)
            .heightIn(min = 140.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(producto.imagenUrl ?: "")
                        .crossfade(true)
                        .build(),
                    contentDescription = producto.nombre
                )
                if (producto.imagenUrl.isNullOrBlank()) {
                    Text("Sin imagen", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                producto.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            producto.descripcion?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                producto.precio.toCLP(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onAdd,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Agregar") }
        }
    }
}

@Composable
private fun HappyHourBanner() {
    var remaining by remember { mutableStateOf("") }

    // Simulamos hora actual y restamos manualmente
    LaunchedEffect(Unit) {
        while (true) {
            val calendar = java.util.Calendar.getInstance()
            val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
            val minute = calendar.get(java.util.Calendar.MINUTE)

            // Hora de término: 2:00 AM
            val endHour = 2
            val endMinute = 0

            // Calculamos minutos restantes (soporte completo API 24+)
            var totalMinutesNow = hour * 60 + minute
            val totalMinutesEnd = endHour + 24 * 60 // 2 AM del día siguiente
            val diffMinutes = totalMinutesEnd - totalMinutesNow

            remaining = if (diffMinutes > 0) {
                val h = diffMinutes / 60
                val m = diffMinutes % 60
                String.format("%02dh %02dm restantes", h, m)
            } else {
                "Finalizó 🎉"
            }

            kotlinx.coroutines.delay(60_000)
        }
    }

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFEA80FC), Color(0xFF7C4DFF))
                )
            )
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "🎉 ¡Happy Hour!",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Descuentos hasta las 2:00 AM",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "⏰ $remaining",
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}