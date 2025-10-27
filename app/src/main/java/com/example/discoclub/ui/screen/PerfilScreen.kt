package com.example.discoclub.ui.screen

// Permiten usar los componentes y herramientas de Jetpack Compose
import androidx.compose.foundation.layout.*                  // Column, Row, Spacer, padding, etc.
import androidx.compose.material3.*                         // Componentes Material Design 3 (TextField, Button, etc.)
import androidx.compose.runtime.*                           // Manejo de estados remember y mutableStateOf
import androidx.compose.ui.Alignment                        // Alineaciones verticales u horizontales
import androidx.compose.ui.Modifier                         // Permite modificar el tamaño y posición de los elementos
import androidx.compose.ui.graphics.Color                   // Permite aplicar colores a los elementos
import androidx.compose.ui.text.font.FontWeight             // Controla el grosor del texto (negrita)
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType          // Define el tipo de teclado (numérico, texto, etc.)
import androidx.compose.ui.text.style.TextAlign             // Alineación de texto
import androidx.compose.ui.unit.dp                          // Define medidas (dp = density pixels)
import com.example.discoclub.ui.viewmodel.AuthViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


// ------------------------------------------------------------
// PANTALLA CONECTADA AL VIEWMODEL (PerfilScreenVm)
// -----------------------------------------------------------
@Composable
fun PerfilScreenVm(
    vm: AuthViewModel,              // Recibe una instancia del ViewModel que contiene la lógica de perfil
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
        onGuardarClick = vm::submitProfileUpdate, // Llama la función del ViewModel cuando se hace clic en “Guardar”
        onCancelarClick = onCancelarClick          // Llama la función recibida cuando se hace clic en “Cancelar”
    )
}

// ------------------------------------------------------------
// PANTALLA: PERFIL DE USUARIO
// ------------------------------------------------------------

@Composable
fun PerfilScreen(
    onGuardarClick: () -> Unit = {},   // Acción opcional al presionar el botón “Guardar cambios”
    onCancelarClick: () -> Unit = {}   // Acción opcional al presionar el botón “Cancelar”
) {
    // ---------------- ESTADOS ----------------
    // Cada variable representa el valor que el usuario escribe en los campos del formulario
    var nombre by remember { mutableStateOf("") }            // Campo: nombre del usuario
    var correo by remember { mutableStateOf("") }            // Campo: correo electrónico
    var telefono by remember { mutableStateOf("") }          // Campo: número de teléfono
    var rol by remember { mutableStateOf("") }               // Campo: rol o tipo de usuario (admin, cliente, etc.)

    //----------------- VARIABLES DE ERROR ---------------------------
    var correoError by remember { mutableStateOf<String?>(null) }
    var telefonoError by remember { mutableStateOf<String?>(null) }
    var rolError by remember { mutableStateOf<String?>(null) }
    // ---------------- CONTENEDOR PRINCIPAL ----------------
    Column(
        modifier = Modifier
            .fillMaxSize()                                   // Ocupa toda la pantalla
            .padding(24.dp),                                 // Margen interno
        horizontalAlignment = Alignment.CenterHorizontally   // Centra el contenido horizontalmente
    ) {
        // ---------------- TÍTULO PRINCIPAL ----------------
        Text(
            text = "Editar Perfil de Usuario",               // Texto del título
            style = MaterialTheme.typography.titleLarge,     // Tamaño grande de título
            fontWeight = FontWeight.Bold,                    // Negrita
            color = Color(0xFF6A1B9A),                       // Color morado institucional
            textAlign = TextAlign.Center                     // Alineación centrada
        )

        Spacer(modifier = Modifier.height(16.dp))            // Espacio entre título y formulario

        // ---------------- DESCRIPCIÓN ----------------
        Text(
            text = "Modifica la información del usuario y presiona Guardar para aplicar los cambios.",
            textAlign = TextAlign.Center,                    // Texto centrado
            color = Color.DarkGray                           // Color gris suave
        )

        Spacer(modifier = Modifier.height(24.dp))            // Espacio antes de los campos

        // ---------------- CAMPO: NOMBRE ----------------
        OutlinedTextField(
            value = nombre,                                  // Valor actual del campo
            onValueChange = { nombre = it },                 // Actualiza el valor cuando el usuario escribe
            label = { Text("Nombre completo") },             // Etiqueta visible sobre el campo
            modifier = Modifier.fillMaxWidth()               // Ocupa todo el ancho disponible
        )

        Spacer(modifier = Modifier.height(12.dp))            // Espacio entre campos

        // ---------------- CAMPO: CORREO ----------------
        OutlinedTextField(
            value = correo,
            onValueChange = {
                correo = it
                correoError = if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                    "Formato de correo inválido"
                } else null
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


        // ---------------- CAMPO: TELÉFONO ----------------
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


        // ---------------- CAMPO: ROL ----------------
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


        // ---------------- FILA DE BOTONES ----------------
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween // Separa los botones izquierda/derecha
        ) {
            // Botón Cancelar
            OutlinedButton(
                onClick = onCancelarClick,                   // Acción al presionar
                modifier = Modifier.weight(1f)               // Ocupa la mitad de la fila
            ) {
                Text("Cancelar")                             // Texto dentro del botón
            }

            Spacer(modifier = Modifier.width(16.dp))         // Espacio entre los botones

            // Botón Guardar
            // Botón Guardar
            Button(
                onClick = {
                    // Validar antes de guardar
                    correoError = if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches())
                        "Formato de correo inválido" else null
                    telefonoError = if (telefono.isBlank()) "El teléfono es obligatorio" else null
                    rolError = if (rol.isBlank()) "El rol es obligatorio" else null

                    // Si no hay errores, guarda
                    if (correoError == null && telefonoError == null && rolError == null) {
                        onGuardarClick()
                    }
                },
                modifier = Modifier.weight(1f) // Ocupa la otra mitad de la fila
            ) {
                Text("Guardar")
            }
        }
    }
}