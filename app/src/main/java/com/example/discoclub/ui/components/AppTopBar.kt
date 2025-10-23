package com.example.discoclub.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.runtime.* // remember / mutableStateOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable //composable reutilizable barra superior
fun AppTopBar(
    openDrawer: () -> Unit, //abre el drawer (menu hamburguesa)
    onHome: () -> Unit, //navega a home
    onLogin: () -> Unit, //navega a login
    onRegister: () -> Unit, //navega a registro
){
    //aca se crea una variable de estado recordada que le dice a la interfaz
    //si el menú desplegable de 3 puntitos debe estar visibile (true) o oculto (false).
    var  showMenu by remember { mutableStateOf(false) } //estado del menu overflow

    CenterAlignedTopAppBar( //barra alineada al centro
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        title = { //slot del titulo
            Text(
                text = "Demo Navegación Compose",//titulo visible
                style = MaterialTheme.typography.titleLarge, //estilo grande
                maxLines = 1, //asegura una sola línea Int.MAX_VALUE //permite varias lineas
                overflow = TextOverflow.Ellipsis //agrega "..." si no cabe
            )
        },
        navigationIcon = { //icono a la izquierda (hamburguesa)
            IconButton(onClick = openDrawer) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menú" ) //icono
            }
        },
        actions = { //acciones a la derecha (iconos + overflow)
            IconButton(onClick = onHome) { //ir a home
                Icon(Icons.Filled.Home, contentDescription ="Home") //icono home
            }
            IconButton(onClick = onLogin) { //ir al login
                Icon(Icons.Filled.AccountCircle, contentDescription = "Login") //icono login
            }
            IconButton(onClick = onRegister) { //ir al registro
                Icon(Icons.Filled.Person, contentDescription = "Registro") //icono registro
            }
            IconButton(onClick = {showMenu = true}) { //abre menu overflow
                Icon(Icons.Filled.MoreVert, contentDescription = "Más") //icono de 3 puntitos
            }
            DropdownMenu(
                expanded = showMenu, //si esta abierto
                onDismissRequest = {showMenu =false} //cierra al tocar afuera
            ) {
                DropdownMenuItem( //opcion home
                    text = { Text("Home") }, //texto opcion
                    onClick = {showMenu =false; onHome()} //navega y cierra
                )

                DropdownMenuItem( //opcion login
                    text = {Text("Login") },
                    onClick = {showMenu =false; onLogin()}
                )
                DropdownMenuItem( //opcion registro
                    text = {Text("Registro") },
                    onClick = {showMenu = false; onRegister()}
                )
            }
        }
 )
}