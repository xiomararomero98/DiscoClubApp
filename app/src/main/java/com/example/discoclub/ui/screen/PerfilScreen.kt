package com.example.discoclub.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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
