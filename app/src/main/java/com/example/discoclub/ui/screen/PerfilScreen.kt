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
import com.example.discoclub.data.local.user.UserEntity
import com.example.discoclub.ui.viewmodel.AuthViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PerfilScreenVm(
    vm: AuthViewModel,              // ViewModel con la lógica del perfil
    userId: Long,                   // ID del usuario a editar
    onPerfilGuardado: () -> Unit,   // Acción al guardar correctamente
    onCancelarClick: () -> Unit     // Acción al cancelar
) {
    val state by vm.profile.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        vm.clearCurrentUser()   //  Limpia los datos del usuario anterior
        vm.loadUserById(userId) //  Carga los datos del usuario actual
    }

    if (state.success) {
        vm.clearProfileResult()
        onPerfilGuardado()
    }

    PerfilScreen(
        user = UserEntity(
            id = userId,
            name = state.name,
            email = state.email,
            phone = state.phone,
            password = "",
            role = state.role
        ),
        onGuardarClick = { nombre, correo, telefono, rol ->
            val user = UserEntity(
                id = userId,
                name = nombre,
                email = correo,
                phone = telefono,
                password = "",
                role = rol
            )
            vm.updateUser(user)
        },
        onCancelarClick = onCancelarClick
    )
}

@Composable
fun PerfilScreen(
    user: UserEntity? = null,
    onGuardarClick: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onCancelarClick: () -> Unit = {}
) {
    var nombre by remember (user?.id) { mutableStateOf(user?.name ?: "") }
    var correo by remember (user?.id) { mutableStateOf(user?.email ?: "") }
    var telefono by remember (user?.id) { mutableStateOf(user?.phone ?: "") }
    var rol by remember (user?.id) { mutableStateOf(user?.role ?: "") }

    var correoError by remember (user?.id) { mutableStateOf<String?>(null) }
    var telefonoError by remember (user?.id) { mutableStateOf<String?>(null) }
    var rolError by remember (user?.id) { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
        correoError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
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
        telefonoError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
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
        rolError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
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
                    correoError =
                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches())
                            "Formato de correo inválido" else null
                    telefonoError = if (telefono.isBlank()) "El teléfono es obligatorio" else null
                    rolError = if (rol.isBlank()) "El rol es obligatorio" else null

                    if (correoError == null && telefonoError == null && rolError == null) {
                        onGuardarClick(nombre, correo, telefono, rol)
                    }
                },
                modifier = Modifier.weight(1f)
            ) { Text("Guardar") }
        }
    }
}

