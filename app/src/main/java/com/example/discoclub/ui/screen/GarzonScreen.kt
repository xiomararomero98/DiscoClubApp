package com.example.discoclub.ui.screen

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.discoclub.ui.viewmodel.PedidosViewModel
import com.example.discoclub.data.local.pedido.PedidoEntity

@Composable
fun GarzonScreen(viewModel: PedidosViewModel){
    val pedidos by viewModel.pedidosMesa.collectAsState(initial = emptyList())
    var producto by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf(1) }
    var mesaId by remember { mutableStateOf(1L) }

    LaunchedEffect(Unit) {
        viewModel.loadPedidosMesa(mesaId)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Mesa $mesaId", style = MaterialTheme.typography.titleMedium)

        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = producto,
                onValueChange = {producto = it},
                label = {Text("Producto")}
            )
            Spacer(modifier = Modifier.width(10.dp))
            TextField(
                value = cantidad.toString(),
                onValueChange = {cantidad = it.toIntOrNull() ?: 1},
                label = {Text("Cantidad")}
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                viewModel.addPedido(
                    mesaId = mesaId,
                    idProducto = producto.toLongOrNull() ?: 0L,
                    cantidad = cantidad,
                    modificacion = null
                )
                producto = ""
                cantidad = 1
            }) {
                Text("Agregar")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.fillMaxHeight()){
            items(pedidos) {pedido: PedidoEntity ->
                Text("Producto: ${pedido.idProducto} x ${pedido.cantidad}")
            }
        }
    }
}