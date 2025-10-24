package com.example.discoclub.domain.validation

import android.util.Patterns

fun validateEmail(email: String): String?{
    if (email.isBlank()) return "El email es obligatorio"
    val ok = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    return if (!ok) "Formato email inválido" else null
}

//valida que el nombre contenga solo letras y espacios sin numeros

fun validateNameLettersOnly(name: String): String? {
    if (name.isBlank()) return "El nombre es obligatorio"
    val regex = Regex("^[A-Za-zÁÉÍÓÚÑáéíóúñ ]+$")
    return if (!regex.matches(name)) "Solo letras y espacios" else null
}

fun validatePhoneDigitsOnly(phone: String): String? {
    if (phone.isBlank()) return "El teléfono es obligatorio"
    if (!phone.all { it.isDigit() }) return "Sólo números"
    if (phone.length !in 8..15) return  "Debe tener entre 8 y 15 dígitos"
    return null
}
fun validateStrongPassword(pass: String): String? {
    if (pass.isBlank()) return "La contraseña es obligatoria"
    if (pass.length <8 ) return "Minimo 8 caracteres"
    if (!pass.any { it.isUpperCase() }) return  "Debe incluir una mayúscula"
    if (!pass.any { it.isLowerCase() }) return "Debe incluir una minúscula"
    if (!pass.any { it.isDigit() }) return "Debe incluir un número"
    if (!pass.any { it.isLetterOrDigit() }) return "Debe incluir un símbolo"
    if (pass.contains(' '))return "No debe contener espacios"
    return null
}

fun validateConfirm(pass: String, confirm: String): String?{
    if (confirm.isBlank()) return "Confirma tu contraseña"
    return if (pass != confirm) "Las contraseñas no coinciden" else null
}