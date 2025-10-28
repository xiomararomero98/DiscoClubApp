package com.example.discoclub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.discoclub.data.local.user.UserEntity
import com.example.discoclub.data.repository.UserRepository
import com.example.discoclub.domain.validation.validateConfirm
import com.example.discoclub.domain.validation.validateEmail
import com.example.discoclub.domain.validation.validateNameLettersOnly
import com.example.discoclub.domain.validation.validatePhoneDigitsOnly
import com.example.discoclub.domain.validation.validateStrongPassword
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(  //estado de la pantalla login
    val email: String = "",                                // Campo email
    val pass: String = "",                                 // Campo contraseña (texto)
    val emailError: String? = null,                        // Error de email
    val passError: String? = null,                         // (Opcional) error de pass en login
    val isSubmitting: Boolean = false,                     // Flag de carga
    val canSubmit: Boolean = false,                        // Habilitar botón
    val success: Boolean = false,                          // Resultado OK
    val errorMsg: String? = null                           // Error global (credenciales inválidas)
)

data class RegisterUiState(
    val name: String = "",                                 // 1) Nombre
    val email: String = "",                                // 2) Email
    val phone: String = "",                                // 3) Teléfono
    val pass: String = "",                                 // 4) Contraseña
    val confirm: String = "",                              // 5) Confirmación

    val nameError: String? = null,                         // Errores por campo
    val emailError: String? = null,
    val phoneError: String? = null,
    val passError: String? = null,
    val confirmError: String? = null,

    val isSubmitting: Boolean = false,                     // Flag de carga
    val canSubmit: Boolean = false,                        // Habilitar botón
    val success: Boolean = false,                          // Resultado OK
    val errorMsg: String? = null                           // Error global (ej: duplicado)

)
// ----------------- COLECCIÓN EN MEMORIA (solo para la demo) -----------------

//2.- Eliminamos la estructura de DemoUser

class AuthViewModel (
    // ✅ NUEVO: 4.- inyectamos el repositorio real que usa Room/SQLite
    private val repository: UserRepository
): ViewModel(){
    // 3.- Eliminamos Colección **estática** en memoria compartida entre instancias del VM (sin storage persistente)
    // Flujos de estado para observar desde la UI
    private val _login = MutableStateFlow(LoginUiState()) //estado interno (login)
    val  login: StateFlow<LoginUiState> = _login //exposicion inmutable

    private val _register = MutableStateFlow(RegisterUiState()) //estado interno registro
    val register: StateFlow<RegisterUiState> = _register

    // ----------------- LOGIN: handlers y envío -----------------

    fun onLoginEmailChange(value: String){
        _login.update { it.copy(email = value, emailError = validateEmail(value)) }
        recomputeLoginCanSubmit()
    }
    fun onLoginPassChange(value: String){
        _login.update { it.copy(pass = value) }
        recomputeLoginCanSubmit()
    }

    private fun recomputeLoginCanSubmit(){
        val s = _login.value
        val can = s.emailError == null &&
                s.email.isNotBlank() &&
                s.pass.isNotBlank()
        _login.update { it.copy(canSubmit = can) }
    }

    fun submitLogin(){ // accion de login simulacion async
        val s = _login.value //snapshot del estado
        if (!s.canSubmit || s.isSubmitting) return //si no se puede o ya esta cargando, salimos
        viewModelScope.launch { //lanzamos corrutina
            _login.update { it.copy(isSubmitting = true, errorMsg = null, success = false ) } //seteamos loading
            delay(500)

            //6.- Se cambia lo anterior por esto ✅ NUEVO: consulta real a la BD vía repositorio

            val result = repository.login(s.email.trim(), s.pass)

            //interpreta el resultado y actualiza el estado
            _login.update {
                if (result.isSuccess){
                    it.copy(isSubmitting = false, success = true, errorMsg = null) //ok:exito
                }else{
                    it.copy(isSubmitting = false, success = false,
                        errorMsg = result.exceptionOrNull()?.message ?: "Error de autenticación")
                }
            }
        }
    }

    fun clearLoginResult(){  //limpia banderas tras navegar
        _login.update { it.copy(success = false, errorMsg = null) }
    }
    // ----------------- REGISTRO: handlers y envío -----------------

    fun onNameChange(value: String){
        val filtered =value.filter { it.isLetter() || it.isWhitespace() } //filtramos numero /simbolos dejamos solo letras y espacios
        _register.update {
            it.copy(name = filtered, nameError = validateNameLettersOnly(filtered))
        }
        recomputeRegisterCanSubmit()                        // Recalculamos habilitado
    }

    fun onRegisterEmailChange(value: String) {              // Handler del email
        _register.update { it.copy(email = value, emailError = validateEmail(value)) } // Guardamos + validamos
        recomputeRegisterCanSubmit()
    }

    fun onPhoneChange(value: String) {                      // Handler del teléfono
        val digitsOnly = value.filter { it.isDigit() }      // Dejamos solo dígitos
        _register.update {                                  // Guardamos + validamos
            it.copy(phone = digitsOnly, phoneError = validatePhoneDigitsOnly(digitsOnly))
        }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterPassChange(value: String) {               // Handler de la contraseña
        _register.update { it.copy(pass = value, passError = validateStrongPassword(value)) } // Validamos seguridad
        // Revalidamos confirmación con la nueva contraseña
        _register.update { it.copy(confirmError = validateConfirm(it.pass, it.confirm)) }
        recomputeRegisterCanSubmit()
    }

    fun onConfirmChange(value: String) {                    // Handler de confirmación
        _register.update { it.copy(confirm = value, confirmError = validateConfirm(it.pass, value)) } // Guardamos + validamos
        recomputeRegisterCanSubmit()
    }

    private fun recomputeRegisterCanSubmit() {              // Habilitar "Registrar" si todo OK
        val s = _register.value                              // Tomamos el estado actual
        val noErrors = listOf(s.nameError, s.emailError, s.phoneError, s.passError, s.confirmError).all { it == null } // Sin errores
        val filled = s.name.isNotBlank() && s.email.isNotBlank() && s.phone.isNotBlank() && s.pass.isNotBlank() && s.confirm.isNotBlank() // Todo lleno
        _register.update { it.copy(canSubmit = noErrors && filled) } // Actualizamos flag
    }

    fun submitRegister() {                                  // Acción de registro (simulación async)
        val s = _register.value                              // Snapshot del estado
        if (!s.canSubmit || s.isSubmitting) return          // Evitamos reentradas
        viewModelScope.launch {                             // Corrutina
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) } // Loading
            delay(700)                                      // Simulamos IO

            // 7.- Se cambia esto por lo anterior✅ NUEVO: inserta en BD (con teléfono) vía repositorio
            val result = repository.register(
                name = s.name.trim(),
                email = s.email.trim(),
                phone = s.phone.trim(),                     // Incluye teléfono
                password = s.pass
            )

            // Interpreta resultado y actualiza estado
            _register.update {   //cambia el estado del formulario ,actualizando los valores por pantalla
                if (result.isSuccess) {     //ve si el registro fue exitoso ,decidiendo que mensaje poner
                    it.copy(isSubmitting = false, success = true, errorMsg = null)  // OK
                } else {
                    it.copy(isSubmitting = false, success = false,    //crea una nueva version del estado ,actualiza la ui
                        errorMsg = result.exceptionOrNull()?.message ?: "No se pudo registrar")     //muestra mensaje de error si falla el registro
                }
            }
        }
    }

    fun clearRegisterResult() {                             // Limpia banderas tras navegar
        _register.update { it.copy(success = false, errorMsg = null) }
    }

    // ---------------------------------------------------------
    //  PERFIL DE USUARIO
    // ---------------------------------------------------------

    data class ProfileState(
        val name: String = "",
        val email: String = "",
        val phone: String = "",
        val role: String = "",
        val success: Boolean = false
    )

    private val _profile = MutableStateFlow(ProfileState())
    val profile: StateFlow<ProfileState> = _profile

    fun submitProfileUpdate() {
        viewModelScope.launch {
            delay(1000) // simula guardado
            _profile.update { it.copy(success = true) }
        }
    }

    fun clearProfileResult() {
        _profile.update { it.copy(success = false) }
    }

    // Obtener todos los usuarios para mostrar en admin y perfiles
    fun getAllUsers() = repository.getAllUsers()

    // Esta función busca un usuario por su ID y carga sus datos en el estado del perfil.
    // Se usa cuando el admin entra a "Editar perfil", así puede ver los datos actuales.
    fun getUserById(id: Int) {
        viewModelScope.launch { // Lanzamos una corrutina (proceso en segundo plano)
            val user = repository.getUserById(id) // Le pedimos al repositorio que busque el usuario
            user?.let {
                // Si lo encuentra, actualizamos el estado del perfil con los datos del usuario
                _profile.update {
                    it.copy(
                        name = user.name,
                        email = user.email,
                        phone = user.phone,
                        role = user.role ?: "" // Si no tiene rol, dejamos el campo vacío
                    )
                }
            }
        }
    }

    // Esta función actualiza los datos del usuario en la base de datos.
    // Se llama cuando el admin presiona "Guardar cambios" en la pantalla de edición.
    fun updateUser(user: UserEntity) {
        viewModelScope.launch { // Corrutina para ejecutar en segundo plano
            repository.updateUser(user) // Le decimos al repositorio que actualice el usuario en Room
            _profile.update { it.copy(success = true) } // Cambiamos el estado para mostrar mensaje de éxito
        }
    }

    // Esta función elimina un usuario de la base de datos.
    // Se usa si el admin decide borrar el perfil de un empleado.
    fun deleteUser(user: UserEntity) {
        viewModelScope.launch { // Corrutina para no bloquear la app
            repository.deleteUser(user) // Llamamos al repositorio para eliminar el usuario
        }
    }

}
