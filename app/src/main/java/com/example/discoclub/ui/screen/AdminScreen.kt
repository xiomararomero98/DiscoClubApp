package com.example.discoclub.ui.screen
// Estos imports permiten usar funciones y componentes de Jetpack Compose
import androidx.compose.foundation.layout.*                 // Column, Row, Box y paddings
import androidx.compose.foundation.lazy.LazyColumn           // Lista desplazable vertical
import androidx.compose.foundation.lazy.items                // Permite recorrer una lista dentro de LazyColumn
import androidx.compose.foundation.text.KeyboardOptions      // Controla el tipo de teclado (numérico, texto, etc.)
import androidx.compose.material.icons.Icons                 // Conjunto de íconos Material Design
import androidx.compose.material.icons.filled.Add            // Ícono con símbolo "+"
import androidx.compose.material.icons.filled.Delete         // Ícono con símbolo "Eliminar"
import androidx.compose.material.icons.filled.Edit           // Ícono con símbolo "Editar"
import androidx.compose.material3.*                          // Componentes Material Design 3 (Scaffold, Card, Button, etc.)
import androidx.compose.runtime.*                            // Manejo de estados: remember, mutableStateOf
import androidx.compose.ui.Alignment                         // Permite alinear elementos dentro de la pantalla
import androidx.compose.ui.Modifier                          // Permite modificar elementos visuales (padding, tamaño, etc.)
import androidx.compose.ui.text.font.FontWeight              // Controla el grosor del texto (negrita)
import androidx.compose.ui.text.input.KeyboardType           // Define el tipo de teclado (texto, numérico, etc.)
import androidx.compose.ui.text.style.TextAlign              // Define la alineación del texto
import androidx.compose.ui.unit.dp                           // Unidad de medida “dp” (píxeles independientes de densidad)
import androidx.compose.ui.graphics.Color                    // Permite aplicar colores
import androidx.compose.animation.AnimatedVisibility          // Permite animar la visibilidad de elementos (mostrar/ocultar)
import androidx.navigation.NavHostController                 // Controlador de navegación
import com.example.discoclub.ui.viewmodel.AuthViewModel       // ViewModel con la lógica de autenticación y perfiles
import kotlinx.coroutines.launch                                // Permite lanzar corrutinas en el ciclo de vida de la app
import androidx.compose.animation.core.tween                    // Permite definir una animación de tiempo
import androidx.compose.animation.fadeIn                        // Permite una animación de aparición
import androidx.compose.animation.fadeOut                       // Permite una animación de desaparecimiento
import androidx.compose.animation.slideInVertically             // Permite una animación de deslizamiento hacia arriba
import androidx.compose.animation.slideOutVertically            // Permite una animación de deslizamiento hacia abajo
import androidx.compose.foundation.shape.RoundedCornerShape     // Permite aplicar esquinas redondeadas
import androidx.compose.ui.unit.sp                              // Permite cambiar el tamaño de la fuente
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel


// ------------------------------------------------------------
// PANTALLA PRINCIPAL: PANEL DE ADMINISTRACIÓN
// ------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class) // Indica que se usan funciones experimentales de Material 3
@Composable // Indica que esta función genera una parte visual (UI)
fun AdminScreen(
    navController: NavHostController,   // Controlador para manejar navegación entre pantallas
    vm: AuthViewModel                   // ViewModel compartido (para obtener usuarios registrados)
) {
    // Variable que guarda el índice de la pestaña seleccionada
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Lista con los nombres de las pestañas que se mostrarán
    val tabs = listOf("Inventario", "Perfiles", "Registros")

    // Scaffold define la estructura base de la pantalla (TopBar, FAB, contenido)
    Scaffold(
        topBar = {
            // Barra superior con título
            TopAppBar(
                title = { Text("Panel de Administración", fontWeight = FontWeight.Bold) }
            )
        },
        floatingActionButton = {
            // Botón flotante que solo aparece en la pestaña de Inventario
            if (selectedTabIndex == 0) {
                FloatingActionButton(onClick = { /* Acción del botón */ }) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar")
                }
            }
        }
    ) { paddingValues ->
        // Contenedor principal (columna vertical)
        Column(
            modifier = Modifier
                .fillMaxSize()                // La columna ocupa todo el alto disponible
                .padding(paddingValues)       // Respeta los márgenes del Scaffold
        ) {
            // ----------------- Pestañas superiores -----------------
            TabRow(selectedTabIndex = selectedTabIndex) {
                // Crea una pestaña por cada título en la lista “tabs”
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index, // Marca la pestaña activa
                        onClick = { selectedTabIndex = index }, // Cambia de pestaña al hacer clic
                        text = { Text(title) } // Muestra el nombre de la pestaña
                    )
                }
            }

            // ----------------- Contenido dinámico -----------------
            // Cambia automáticamente según la pestaña activa
            when (selectedTabIndex) {
                0 -> AdminInventarioScreen(vm = vm)                       // Pestaña Inventario
                1 -> AdminPerfilesScreen(vm = vm, navController)   // Pestaña Perfiles (usa el ViewModel)
                2 -> AdminRegistrosScreen()                        // Pestaña Registros
            }
        }
    }
}

// INVENTARIO (CRUD DE PRODUCTOS)

@Composable
fun AdminInventarioScreen(vm: AuthViewModel) {
    // Campos del formulario
    var nombre by remember { mutableStateOf("") }            // Campo: nombre del producto
    var descripcion by remember { mutableStateOf("") }       // Campo: descripción del producto
    var precio by remember { mutableStateOf("") }            // Campo: precio del producto
    var stock by remember { mutableStateOf("") }             // Campo: cantidad en stock
    var imagen by remember { mutableStateOf("") }            // Campo: URL de imagen del producto

    val state by vm.register.collectAsStateWithLifecycle()
    var productos by remember { mutableStateOf(listOf<String>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Gestión de Inventario",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6A1B9A)
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text("Inventario actual:", fontWeight = FontWeight.SemiBold)

        if (productos.isEmpty()) {
            Text("No hay productos registrados aún.", color = Color.Gray)
        } else {
            LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
                items(productos) { producto ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• $producto")
                        IconButton(onClick = { productos = productos - producto }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = Color.Red)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Agregar Producto", fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nombre.isBlank() && state.error != null
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = descripcion.isBlank() && state.error != null
                )

                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = { Text("Precio (CLP) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    isError = precio.isBlank() && state.error != null
                )

                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    isError = stock.isBlank() && state.error != null
                )

                OutlinedTextField(
                    value = imagen,
                    onValueChange = { imagen = it },
                    label = { Text("Imagen (URL) *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = imagen.isBlank() && state.error != null
                )

                if (state.error != null) {
                    Text(
                        text = state.error ?: "",
                        color = Color.Red,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (nombre.isBlank() || descripcion.isBlank() ||
                            precio.isBlank() || stock.isBlank() || imagen.isBlank()
                        ) {
                            vm.setError("Todos los campos son obligatorios")
                        } else {
                            productos = productos + "$nombre - $$precio"
                            vm.setError(null)
                            nombre = ""
                            descripcion = ""
                            precio = ""
                            stock = ""
                            imagen = ""
                        }
                    },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}


// PERFILES DE USUARIO

@Composable
fun AdminPerfilesScreen(
    vm: AuthViewModel,                      // ViewModel inyectado (usa la BD de usuarios)
    navController: NavHostController        // Controlador para navegar a PerfilScreen
) {
    // Observamos los usuarios desde la base de datos (flujo reactivo)
    val usuarios by vm.getAllUsers().collectAsState(initial = emptyList())
    // Controla el usuario seleccionado para eliminar
    var userToDelete by remember {
        mutableStateOf<com.example.discoclub.data.local.user.UserEntity?>(
            null
        )
    }

    // snackbar configuracion
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                //animacion para el mensaje

                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
                ) {
                    Snackbar(
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        containerColor = Color(0xFF6A1B9A), // aca dice que su fondo será de color morado
                        contentColor = Color.White,
                        shape = RoundedCornerShape(16.dp),   // las esquinas del contorno no serán tan intensas
                        actionOnNewLine = false
                    ) {
                        // Centrado y estilo de texto
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = data.visuals.message,
                                fontSize = 16.sp,              // aca esta el tamaño de la letra
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        // Contenedor principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Gestión de Perfiles de Usuario",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6A1B9A),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Aquí podrás visualizar, editar o eliminar perfiles registrados.",
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Si no hay usuarios registrados
            if (usuarios.isEmpty()) {
                Text("No hay usuarios registrados aún.", color = Color.Gray)
            } else {
                // Lista de usuarios registrados
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(usuarios) { user ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Muestra los datos del usuario
                                Column {
                                    Text(" ${user.name}", fontWeight = FontWeight.Bold)
                                    Text(" ${user.email}")
                                    Text(" ${user.phone}")
                                    Text(" ${user.role ?: "Sin rol"}")
                                }

                                Column(
                                    horizontalAlignment = Alignment.End,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    // Botón Editar
                                    IconButton(onClick = {
                                        // Navega al formulario de edición de perfil, pasando el ID del usuario
                                        navController.navigate("perfil/${user.id}")
                                    }) {
                                        Icon(
                                            Icons.Filled.Edit,
                                            contentDescription = "Editar Perfil",
                                            tint = Color(0xFF6A1B9A)
                                        )
                                    }
                                    // Botón Eliminar
                                    IconButton(onClick = { userToDelete = user }) {
                                        Icon(
                                            Icons.Filled.Delete,
                                            contentDescription = "Eliminar Perfil",
                                            tint = Color(0xFFD32F2F) // aca dice que el color sea rojo
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }


            //  Diálogo de confirmación de eliminación
            if (userToDelete != null) {
                AlertDialog(
                    onDismissRequest = { userToDelete = null },
                    confirmButton = {
                        TextButton(onClick = {
                            vm.deleteUser(userToDelete!!)
                            coroutineScope.launch {       // lanza corrutina para mostrar mensaje
                                snackbarHostState.showSnackbar(
                                    "Perfil eliminado correctamente ",
                                    withDismissAction = true)
                            }
                            userToDelete = null
                        }) {
                            Text("Sí, eliminar", color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { userToDelete = null }) {
                            Text("Cancelar")
                        }
                    },
                    title = { Text("Confirmar eliminación") },
                    text = { Text("¿Estás seguro de que quieres eliminar este perfil?") }
                )
            }
        }
    }
}

//  REGISTROS / REPORTES

@Composable
fun AdminRegistrosScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título de la pantalla
        Text(
            "Reportes y Registros del Sistema",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6A1B9A)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Descripción general
        Text(
            "Aquí podrás visualizar registros de ventas, movimientos o actividades del sistema.",
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Ícono decorativo
        Icon(
            Icons.Filled.Delete,
            contentDescription = "Reportes",
            tint = Color.Gray
        )
    }
}

