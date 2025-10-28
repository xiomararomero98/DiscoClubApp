package com.example.discoclub.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
<<<<<<< HEAD
import androidx.compose.ui.text.input.KeyboardType          // Define el tipo de teclado (numérico, texto, etc.)
import androidx.compose.ui.text.style.TextAlign             // Alineación de texto
import androidx.compose.ui.unit.dp                          // Define medidas (dp = density pixels)
import com.example.discoclub.ui.viewmodel.AuthViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PerfilScreenVm(
    vm: AuthViewModel,              // Recibe una instancia del ViewModel que contiene la lógica de perfil
    userId : Long,                  // ID del usuario que se está editando
    onPerfilGuardado: () -> Unit,   // Función que se ejecutará cuando el perfil se haya guardado correctamente (por ejemplo, volver atrás)
    onCancelarClick: () -> Unit     // Función que se ejecuta al presionar “Cancelar”
) {
    // ---------------------------------------------------------
    // Observa el estado del perfil (ProfileState) que vive en el ViewModel.
    // collectAsStateWithLifecycle() convierte un flujo (StateFlow) en un estado Compose reactivo.
    // Así, si el ViewModel actualiza el estado (por ejemplo, al guardar),
    // la interfaz se vuelve a componer automáticamente con los nuevos datos.
    // ---------------------------------------------------------
    val state by vm.profile.collectAsStateWithLifecycle()
    LaunchedEffect(userId) {
        vm.loadUserById(userId) //  Llama a una función del ViewModel para buscar al usuario en la base de datos
    }
    // ---------------------------------------------------------
    // Verifica si el perfil se guardó correctamente.
    // El campo "success" del estado cambia a true cuando la actualización
    // en el ViewModel termina (por ejemplo, después de guardar en base de datos o API).
    // ---------------------------------------------------------
    if (state.success) {
        // Limpia el resultado para evitar que el “éxito” quede marcado al volver a la pantalla.
        vm.clearProfileResult()
        // Llama a la función que definiste en el NavGraph (por ejemplo: volver atrás o mostrar mensaje).
        onPerfilGuardado()
    }
    // ---------------------------------------------------------
    // Muestra la pantalla visual PerfilScreen.
    // Se le pasan las acciones (callbacks) que debe ejecutar al guardar o cancelar.
    // En este caso, cuando el usuario presiona “Guardar”:
    // → Se llama a vm.submitProfileUpdate(), que actualiza los datos en el ViewModel.
    // Cuando presiona “Cancelar”:
    // → Se ejecuta la función onCancelarClick(), que normalmente vuelve a la pantalla anterior.
    // ---------------------------------------------------------
    PerfilScreen(
        user = state.user,// aca pasamos el usuario cargado desde la base de datosh
        onGuardarClick = { nombre, correo, telefono, rol ->
            // Crea un usuario actualizado con los nuevos datos
            val user = state.user?.copy(
                id = 0L, //  luego lo cambiamos según el usuario real seleccionado
                name = nombre,
                email = correo,
                phone = telefono,
                password = "", // (no se toca aquí)
                role = rol
            )
            if (user != null) {
                vm.updateUser(user) // Llama al repositorio para guardar cambios
            }
        },
        onCancelarClick = onCancelarClick
    )
}

// Perfil de usuario

@Composable
fun PerfilScreen(
    user: UserEntity? = null,
    onGuardarClick: (String, String, String, String) -> Unit = { _, _, _, _ -> },// Acción al presionar el botón “Guardar”
    onCancelarClick: () -> Unit = {}   // Acción opcional al presionar el botón “Cancelar”
) {
    // ---------------- ESTADOS ----------------
    // Cada variable representa el valor que el usuario escribe en los campos del formulario
    var nombre by remember { mutableStateOf(user?.name ?:"")}            // Campo: nombre del usuario
    var correo by remember { mutableStateOf(user?.email ?: "") }            // Campo: correo electrónico
    var telefono by remember { mutableStateOf(user?.phone ?: "") }          // Campo: número de teléfono
    var rol by remember { mutableStateOf(user?.role ?: "") }               // Campo: rol o tipo de usuario (admin, cliente, etc.)
=======
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PerfilScreen(
    onGuardarClick: () -> Unit = {},   // Acción al presionar “Guardar cambios”
    onCancelarClick: () -> Unit = {},  // Acción al presionar “Cancelar”
    onLogout: () -> Unit = {}          // Acción al presionar “Cerrar sesión”
) {
    // ---------------- ESTADOS DEL FORM ----------------
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("") }
>>>>>>> e82c16492ec21d1b70a49a6f7cbfe4beb62ddac4

    // ---------------- ERRORES ----------------
    var correoError by remember { mutableStateOf<String?>(null) }
    var telefonoError by remember { mutableStateOf<String?>(null) }
    var rolError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título
        Text(
            text = "Editar Perfil de Usuario",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6A1B9A),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Modifica la información del usuario y presiona Guardar para aplicar los cambios.",
            textAlign = TextAlign.Center,
            color = Color.DarkGray
        )

        Spacer(Modifier.height(24.dp))

        // Nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // Correo
        OutlinedTextField(
            value = correo,
            onValueChange = {
                correo = it
                correoError =
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches())
                        "Formato de correo inválido" else null
            },
            label = { Text("Correo electrónico") },
            isError = correoError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        if (correoError != null) {
            Text(
                text = correoError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(Modifier.height(12.dp))

        // Teléfono
        OutlinedTextField(
            value = telefono,
            onValueChange = {
                telefono = it.filter { c -> c.isDigit() }
                telefonoError = if (telefono.isBlank()) "El teléfono es obligatorio" else null
            },
            label = { Text("Teléfono") },
            isError = telefonoError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        if (telefonoError != null) {
            Text(
                text = telefonoError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(Modifier.height(12.dp))

        // Rol
        OutlinedTextField(
            value = rol,
            onValueChange = {
                rol = it
                rolError = if (rol.isBlank()) "El rol es obligatorio" else null
            },
            label = { Text("Rol (Administrador, Cliente, etc.)") },
            isError = rolError != null,
            modifier = Modifier.fillMaxWidth()
        )
        if (rolError != null) {
            Text(
                text = rolError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(Modifier.height(20.dp))

        // Botones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onCancelarClick,
                modifier = Modifier.weight(1f)
            ) { Text("Cancelar") }

            Button(
                onClick = {
                    // Validaciones antes de guardar
                    correoError =
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches())
                            "Formato de correo inválido" else null
                    telefonoError = if (telefono.isBlank()) "El teléfono es obligatorio" else null
                    rolError = if (rol.isBlank()) "El rol es obligatorio" else null

                    if (correoError == null && telefonoError == null && rolError == null) {
                        onGuardarClick()
                    }
                },
                modifier = Modifier.weight(1f)
            ) { Text("Guardar") }

            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.weight(1f)
            ) { Text("Cerrar sesión") }
        }
    }
}
